package com.example.warehouse_accounting_app.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.core.ui.components.AccessDeniedScreen
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.core.ui.components.SessionExpiredScreen
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── ViewModel ────────────────────────────────────────────────────────────────

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
 * Используется в [RoleGuard].
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
                GuardState.UnknownError("Не удалось проверить права доступа")
        }
}

// ── Composable guard ─────────────────────────────────────────────────────────

/**
 * Защищает экран по роли пользователя.
 *
 * - Пока роль загружается — [LoadingContent].
 * - Успех и [allowed] — [content].
 * - Успех, но роль не подходит — [AccessDeniedScreen].
 * - 401 или «конец сессии» по /me — [SessionExpiredScreen] (токен уже очищен), затем [onSessionExpired].
 * - Прочий 403 по /me — сообщение об ошибке, токен сохранён, можно [retry].
 * - Сеть / сервер / прочее — [ErrorContent] с понятными сообщениями.
 *
 * @param onSessionExpired перейти на экран входа и сбросить стек (например [NavHostController.logout]).
 */
@Composable
fun RoleGuard(
    viewModelFactory: WarehouseViewModelFactory,
    allowed: (UserRole) -> Boolean,
    onBack: () -> Unit,
    onSessionExpired: () -> Unit,
    content: @Composable () -> Unit,
) {
    val vm: RouteGuardViewModel = viewModel(factory = viewModelFactory)
    val state by vm.state.collectAsStateWithLifecycle()

    when (val s = state) {
        GuardState.Loading -> LoadingContent()
        GuardState.Unauthorized -> SessionExpiredScreen(onGoToLogin = onSessionExpired)
        is GuardState.ProfileAccessDenied ->
            GuardErrorScaffold(title = "Доступ", onBack = onBack) {
                ErrorContent(
                    message = s.message,
                    onRetry = { vm.retry() },
                    onSecondaryAction = onBack,
                    secondaryActionLabel = "Вернуться",
                )
            }
        is GuardState.NetworkError ->
            GuardErrorScaffold(title = "Нет связи", onBack = onBack) {
                ErrorContent(
                    message = s.message,
                    onRetry = { vm.retry() },
                    onSecondaryAction = onBack,
                )
            }
        is GuardState.ServerError ->
            GuardErrorScaffold(title = "Ошибка", onBack = onBack) {
                ErrorContent(
                    message = s.message,
                    onRetry = null,
                    onSecondaryAction = onBack,
                )
            }
        is GuardState.UnknownError ->
            GuardErrorScaffold(title = "Ошибка", onBack = onBack) {
                ErrorContent(
                    message = s.message,
                    onRetry = null,
                    onSecondaryAction = onBack,
                )
            }
        is GuardState.Loaded -> {
            if (allowed(s.role)) {
                content()
            } else {
                AccessDeniedScreen(onBack = onBack)
            }
        }
    }
}

@Composable
private fun GuardErrorScaffold(
    title: String,
    onBack: () -> Unit,
    main: @Composable () -> Unit,
) {
    AppScaffold(
        topBar = {
            AppTopBar(title = title, onBack = onBack)
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                main()
            }
        },
    )
}
