package com.example.warehouse_accounting_app.presentation.splash

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.usecase.auth.CheckAuthStateUseCase
import com.example.warehouse_accounting_app.testutil.FakeAuthRepository
import com.example.warehouse_accounting_app.testutil.MainDispatcherRule
import com.example.warehouse_accounting_app.testutil.sampleUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    @Test
    fun startCheck_navigates_to_dashboard_for_authenticated_user() {
        runTest {
            val repository = FakeAuthRepository(currentUserResult = AppResult.Success(sampleUser()))
                .apply { setToken("valid-jwt") }
            val viewModel = SplashViewModel(CheckAuthStateUseCase(repository))

            viewModel.startCheck()

            assertEquals(SplashDestination.Dashboard, viewModel.destination.value)
        }
    }

    @Test
    fun startCheck_navigates_to_login_without_token() {
        runTest {
            val repository = FakeAuthRepository().apply { setToken(null) }
            val viewModel = SplashViewModel(CheckAuthStateUseCase(repository))

            viewModel.startCheck()

            assertEquals(SplashDestination.Login, viewModel.destination.value)
        }
    }
}
