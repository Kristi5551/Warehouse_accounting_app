package com.example.warehouse_accounting_app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.ui.components.AccessDeniedScreen
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.core.ui.components.SessionExpiredScreen
import com.example.warehouse_accounting_app.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.presentation.access.GuardState
import com.example.warehouse_accounting_app.presentation.access.RouteGuardViewModel

/**
 * Защищает экран по роли пользователя.
 *
 * - Пока роль загружается — [LoadingContent].
 * - Успех и [allowed] — [content].
 * - Успех, но роль не подходит — [AccessDeniedScreen].
 * - 401 или «конец сессии» по /me — [SessionExpiredScreen] (токен уже очищен), затем [onSessionExpired].
 * - Прочий 403 по /me — сообщение об ошибке, токен сохранён, можно retry.
 * - Сеть / сервер / прочее — [ErrorContent] с понятными сообщениями.
 *
 * @param onSessionExpired перейти на экран входа и сбросить стек.
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
