package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.testutil.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class LogoutUseCaseTest {

    @Test
    fun invoke_clears_user_session() {
        runTest {
            val repository = FakeAuthRepository().apply { setToken("active-jwt") }

            LogoutUseCase(repository).invoke()

            assertTrue(repository.logoutCalled)
        }
    }
}
