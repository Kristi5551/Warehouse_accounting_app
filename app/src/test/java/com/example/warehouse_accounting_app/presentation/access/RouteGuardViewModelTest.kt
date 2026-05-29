package com.example.warehouse_accounting_app.presentation.access

import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.testutil.FakeAuthRepository
import com.example.warehouse_accounting_app.testutil.MainDispatcherRule
import com.example.warehouse_accounting_app.testutil.sampleUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RouteGuardViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    @Test
    fun load_exposes_current_user_role_for_protected_route() {
        runTest {
            val repository = FakeAuthRepository(
                currentUserResult = AppResult.Success(sampleUser(role = UserRole.MANAGER)),
            )
            val viewModel = RouteGuardViewModel(GetCurrentUserUseCase(repository))

            val state = viewModel.state.value
            assertTrue(state is GuardState.Loaded)
            assertEquals(UserRole.MANAGER, (state as GuardState.Loaded).role)
        }
    }

    @Test
    fun load_returns_unauthorized_on_expired_session() {
        runTest {
            val repository = FakeAuthRepository(
                currentUserResult = AppResult.Error(
                    message = "Сессия истекла",
                    appError = AppError.Unauthorized("Сессия истекла"),
                ),
            )
            val viewModel = RouteGuardViewModel(GetCurrentUserUseCase(repository))

            assertEquals(GuardState.Unauthorized, viewModel.state.value)
        }
    }
}
