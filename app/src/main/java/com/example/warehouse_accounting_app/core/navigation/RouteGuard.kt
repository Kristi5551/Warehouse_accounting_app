package com.example.warehouse_accounting_app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.core.ui.components.AccessDeniedScreen
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── ViewModel ────────────────────────────────────────────────────────────────

sealed interface GuardState {
    data object Loading : GuardState
    data class Loaded(val role: UserRole) : GuardState
    data object Failed : GuardState
}

/**
 * Лёгкая ViewModel для проверки роли текущего пользователя перед открытием экрана.
 * Используется в [RoleGuard].
 */
class RouteGuardViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<GuardState>(GuardState.Loading)
    val state: StateFlow<GuardState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = when (val r = getCurrentUser()) {
                is AppResult.Success -> GuardState.Loaded(r.data.role)
                is AppResult.Error -> GuardState.Failed
            }
        }
    }
}

// ── Composable guard ─────────────────────────────────────────────────────────

/**
 * Защищает экран по роли пользователя.
 *
 * - Пока роль загружается — показывает [LoadingContent].
 * - Если роль подходит под [allowed] — рендерит [content].
 * - Иначе — показывает [AccessDeniedScreen].
 *
 * @param viewModelFactory фабрика для создания [RouteGuardViewModel].
 * @param allowed предикат по роли: `true` = доступ разрешён.
 * @param onBack действие «назад» из [AccessDeniedScreen].
 * @param content защищённый контент.
 */
@Composable
fun RoleGuard(
    viewModelFactory: WarehouseViewModelFactory,
    allowed: (UserRole) -> Boolean,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    val vm: RouteGuardViewModel = viewModel(factory = viewModelFactory)
    val state by vm.state.collectAsStateWithLifecycle()

    when (val s = state) {
        GuardState.Loading -> LoadingContent()
        GuardState.Failed -> AccessDeniedScreen(onBack = onBack)
        is GuardState.Loaded -> {
            if (allowed(s.role)) content()
            else AccessDeniedScreen(onBack = onBack)
        }
    }
}
