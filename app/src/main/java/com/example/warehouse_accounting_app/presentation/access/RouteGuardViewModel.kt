package com.example.warehouse_accounting_app.presentation.access

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.presentation.common.toUserMessage
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GuardState {
    data object Loading : GuardState

    data class Loaded(val role: UserRole) : GuardState

    data object Unauthorized : GuardState

    data class ProfileAccessDenied(val message: String) : GuardState

    data class NetworkError(val message: String) : GuardState

    data class ServerError(val message: String) : GuardState

    data class UnknownError(val message: String) : GuardState
}

class RouteGuardViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<GuardState>(GuardState.Loading)
    val state: StateFlow<GuardState> = _state.asStateFlow()

    init {
        load()
    }

    fun retry() {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = GuardState.Loading
            _state.value =
                when (val r = getCurrentUser()) {
                    is AppResult.Success -> GuardState.Loaded(r.data.role)
                    is AppResult.Error -> mapError(r)
                }
        }
    }

    private fun mapError(r: AppResult.Error): GuardState =
        when (r.appError) {
            is AppError.SessionExpired, is AppError.Unauthorized -> GuardState.Unauthorized
            is AppError.Forbidden ->
                GuardState.ProfileAccessDenied(r.toUserMessage("Недостаточно прав"))
            is AppError.Network ->
                GuardState.NetworkError(r.toUserMessage())
            is AppError.Server ->
                GuardState.ServerError(r.toUserMessage())
            is AppError.Validation, is AppError.NotFound, is AppError.Conflict, is AppError.Unknown ->
                GuardState.UnknownError(
                    r.toUserMessage("Не удалось загрузить профиль. Проверьте подключение к сети и попробуйте снова."),
                )
        }
}
