package com.example.warehouse_accounting_app.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.ApproveUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.BlockUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.ChangeUserRoleUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.CreateAdminUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.GetPendingUsersUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.GetUsersUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.UnblockUserUseCase
import com.example.warehouse_accounting_app.presentation.common.toUserMessage
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
    private val createAdminUser: CreateAdminUserUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()

    private var allUsers: List<User> = emptyList()

    init {
        viewModelScope.launch {
            when (val r = getCurrentUser()) {
                is AppResult.Success -> _state.update { it.copy(currentUserId = r.data.id) }
                else -> Unit
            }
            load()
        }
    }

    private fun isSessionEnded(err: AppResult.Error): Boolean =
        err.appError is AppError.Unauthorized || err.appError is AppError.SessionExpired

    private fun userListErrorMessage(r: AppResult.Error): String =
        when (r.appError) {
            is AppError.Forbidden -> r.appError.toUserMessage("Недостаточно прав")
            is AppError.NotFound -> r.appError.toUserMessage("Пользователь не найден")
            else -> r.toUserMessage()
        }

    fun onEvent(event: UserListEvent) {
        when (event) {
            is UserListEvent.FilterChanged -> {
                _state.update { it.copy(filter = event.filter) }
                viewModelScope.launch { load() }
            }
            UserListEvent.Refresh -> viewModelScope.launch { load() }
            is UserListEvent.Approve -> performMutation("Пользователь подтверждён") { approveUser(event.id) }
            is UserListEvent.Block -> performMutation("Пользователь заблокирован") { blockUser(event.id) }
            is UserListEvent.Unblock -> performMutation("Пользователь разблокирован") { unblockUser(event.id) }
            is UserListEvent.OpenRoleDialog -> _state.update { it.copy(roleDialogUserId = event.id) }
            UserListEvent.CloseRoleDialog -> _state.update { it.copy(roleDialogUserId = null) }
            is UserListEvent.ConfirmRole -> {
                _state.update { it.copy(roleDialogUserId = null) }
                performMutation("Роль изменена") { changeUserRole(event.userId, event.role) }
            }
            UserListEvent.ClearMessages -> _state.update { it.copy(errorMessage = null, successMessage = null) }
            UserListEvent.SessionExpiredConsumed -> _state.update { it.copy(sessionExpired = false) }
            UserListEvent.OpenCreateAdminDialog ->
                _state.update {
                    it.copy(
                        createAdminDialogVisible = true,
                        createAdminFormError = null,
                        newAdminFullName = "",
                        newAdminEmail = "",
                        newAdminPassword = "",
                        newAdminRepeatPassword = "",
                    )
                }
            UserListEvent.CloseCreateAdminDialog ->
                _state.update {
                    it.copy(
                        createAdminDialogVisible = false,
                        createAdminFormError = null,
                    )
                }
            is UserListEvent.CreateAdminFullNameChanged ->
                _state.update { s -> s.copy(newAdminFullName = event.value, createAdminFormError = null) }
            is UserListEvent.CreateAdminEmailChanged ->
                _state.update { s -> s.copy(newAdminEmail = event.value, createAdminFormError = null) }
            is UserListEvent.CreateAdminPasswordChanged ->
                _state.update { s -> s.copy(newAdminPassword = event.value, createAdminFormError = null) }
            is UserListEvent.CreateAdminRepeatPasswordChanged ->
                _state.update { s -> s.copy(newAdminRepeatPassword = event.value, createAdminFormError = null) }
            UserListEvent.SubmitCreateAdmin -> submitCreateAdmin()
        }
    }

    private fun submitCreateAdmin() {
        viewModelScope.launch {
            val s = _state.value
            val err =
                when {
                    s.newAdminFullName.isBlank() -> "Укажите ФИО"
                    s.newAdminEmail.isBlank() -> "Введите email"
                    s.newAdminPassword.length < 6 -> "Пароль не короче 6 символов"
                    s.newAdminPassword != s.newAdminRepeatPassword -> "Пароли не совпадают"
                    else -> null
                }
            if (err != null) {
                _state.update { it.copy(createAdminFormError = err) }
                return@launch
            }
            _state.update {
                it.copy(createAdminFormError = null, actionInProgress = true, errorMessage = null, successMessage = null)
            }
            when (val r = createAdminUser(s.newAdminFullName, s.newAdminEmail, s.newAdminPassword)) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            actionInProgress = false,
                            createAdminDialogVisible = false,
                            newAdminFullName = "",
                            newAdminEmail = "",
                            newAdminPassword = "",
                            newAdminRepeatPassword = "",
                            successMessage = "Администратор создан и может войти в систему",
                        )
                    }
                    load()
                }
                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            actionInProgress = false,
                            errorMessage = userListErrorMessage(r),
                            sessionExpired = isSessionEnded(r),
                        )
                    }
                }
            }
        }
    }

    private fun performMutation(successText: String, block: suspend () -> AppResult<User>) {
        viewModelScope.launch {
            _state.update { it.copy(actionInProgress = true, errorMessage = null, successMessage = null) }
            when (val r = block()) {
                is AppResult.Success -> {
                    _state.update { it.copy(actionInProgress = false, successMessage = successText) }
                    load()
                }
                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            actionInProgress = false,
                            errorMessage = userListErrorMessage(r),
                            sessionExpired = isSessionEnded(r),
                        )
                    }
                }
            }
        }
    }

    /**
     * Загружает список пользователей с учётом текущего фильтра.
     * - PENDING → GET /api/users/pending (оптимизированный endpoint)
     * - остальные → GET /api/users с клиентской фильтрацией
     */
    private suspend fun load() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        val currentFilter = _state.value.filter
        if (currentFilter == UserListFilter.PENDING) {
            when (val r = getPendingUsers()) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false, users = r.data) }
                }
                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = userListErrorMessage(r),
                            sessionExpired = isSessionEnded(r),
                        )
                    }
                }
            }
        } else {
            when (val r = getUsers()) {
                is AppResult.Success -> {
                    allUsers = r.data
                    _state.update { state ->
                        state.copy(isLoading = false, users = applyFilter(allUsers, state.filter))
                    }
                }
                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = userListErrorMessage(r),
                            sessionExpired = isSessionEnded(r),
                        )
                    }
                }
            }
        }
    }

    private fun applyFilter(users: List<User>, filter: UserListFilter): List<User> =
        users.filter { filter.matchesStatus(it.status) }
}
