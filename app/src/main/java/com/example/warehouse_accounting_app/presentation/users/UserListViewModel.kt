package com.example.warehouse_accounting_app.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.ApproveUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.BlockUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.ChangeUserRoleUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.GetPendingUsersUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.GetUsersUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.UnblockUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserListViewModel(
    private val getUsers: GetUsersUseCase,
    private val getPendingUsers: GetPendingUsersUseCase,
    private val approveUser: ApproveUserUseCase,
    private val blockUser: BlockUserUseCase,
    private val unblockUser: UnblockUserUseCase,
    private val changeUserRole: ChangeUserRoleUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            when (val r = getCurrentUser()) {
                is AppResult.Success -> _state.update { it.copy(currentUserId = r.data.id) }
                else -> Unit
            }
            load()
        }
    }

    fun onEvent(event: UserListEvent) {
        when (event) {
            UserListEvent.Refresh -> viewModelScope.launch { load() }
            UserListEvent.ShowAll -> viewModelScope.launch {
                _state.update { it.copy(filter = UserListFilter.ALL) }
                load()
            }
            UserListEvent.ShowPending -> viewModelScope.launch {
                _state.update { it.copy(filter = UserListFilter.PENDING) }
                load()
            }
            is UserListEvent.Approve -> performMutation { approveUser(event.id) }
            is UserListEvent.Block -> performMutation { blockUser(event.id) }
            is UserListEvent.Unblock -> performMutation { unblockUser(event.id) }
            is UserListEvent.OpenRoleDialog ->
                _state.update { it.copy(roleDialogUserId = event.id) }
            UserListEvent.CloseRoleDialog ->
                _state.update { it.copy(roleDialogUserId = null) }
            is UserListEvent.ConfirmRole -> {
                _state.update { it.copy(roleDialogUserId = null) }
                performMutation { changeUserRole(event.userId, event.role) }
            }
            UserListEvent.ClearMessages ->
                _state.update { it.copy(errorMessage = null, successMessage = null) }
            UserListEvent.SessionExpiredConsumed ->
                _state.update { it.copy(sessionExpired = false) }
        }
    }

    private fun performMutation(block: suspend () -> AppResult<User>) {
        viewModelScope.launch {
            _state.update {
                it.copy(actionInProgress = true, errorMessage = null, successMessage = null)
            }
            when (val r = block()) {
                is AppResult.Success -> {
                    _state.update { it.copy(actionInProgress = false, successMessage = "Готово") }
                    load()
                }
                is AppResult.Error -> {
                    val code = (r.throwable as? ApiException)?.statusCode
                    _state.update {
                        it.copy(
                            actionInProgress = false,
                            errorMessage = r.message,
                            sessionExpired = code == 401,
                        )
                    }
                }
            }
        }
    }

    private suspend fun load() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        val result =
            when (_state.value.filter) {
                UserListFilter.ALL -> getUsers()
                UserListFilter.PENDING -> getPendingUsers()
            }
        when (result) {
            is AppResult.Success ->
                _state.update { it.copy(isLoading = false, users = result.data) }
            is AppResult.Error -> {
                val code = (result.throwable as? ApiException)?.statusCode
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        sessionExpired = code == 401,
                    )
                }
            }
        }
    }
}
