package com.example.warehouse_accounting_app.presentation.access

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GuardState {
    data object Loading : GuardState

    /** Профиль загружен; отказ в доступе — только если [RoleGuard] не пропускает роль. */
    data class Loaded(val role: UserRole) : GuardState

    /** 401 или закрытая учётная запись на /me — токен очищен в репозитории. */
    data object Unauthorized : GuardState

    /** /me вернул 403 без признаков «конец сессии» — токен сохранён (редкий случай). */
    data class ProfileAccessDenied(val message: String) : GuardState

    data class NetworkError(val message: String) : GuardState

    data class ServerError(val message: String) : GuardState

    data class UnknownError(val message: String) : GuardState
}

/**
 * ViewModel для проверки актуальной роли ([GET /api/auth/me]) перед открытием экрана.
 * Используется в [com.example.warehouse_accounting_app.core.navigation.RoleGuard].
 */
class RouteGuardViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<GuardState>(GuardState.Loading)
    val state: StateFlow<GuardState> = _state.asStateFlow()

    init {
        load()
    }

    /** Повторная загрузка профиля (например, после сетевой ошибки). */
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
            is AppError.Forbidden -> GuardState.ProfileAccessDenied(r.message)
            is AppError.Network ->
                GuardState.NetworkError("Нет соединения с сервером")
            is AppError.Server ->
                GuardState.ServerError("Ошибка сервера. Попробуйте позже")
            is AppError.Unknown, null ->
                GuardState.UnknownError("Не удалось загрузить профиль. Проверьте подключение к сети и попробуйте снова.")
        }
}
