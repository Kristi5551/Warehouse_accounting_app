package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.testutil.FakeAuthRepository
import com.example.warehouse_accounting_app.testutil.sampleUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckAuthStateUseCaseTest {

    @Test
    fun invoke_returns_authenticated_when_token_and_profile_valid() {
        runTest {
            val user = sampleUser()
            val repository = FakeAuthRepository(currentUserResult = AppResult.Success(user))
                .apply { setToken("jwt-token") }
            val useCase = CheckAuthStateUseCase(repository)

            val result = useCase()

            assertTrue(result is AuthCheckResult.Authenticated)
            assertEquals(user, (result as AuthCheckResult.Authenticated).user)
        }
    }

    @Test
    fun invoke_returns_unauthenticated_when_token_missing() {
        runTest {
            val repository = FakeAuthRepository().apply { setToken(null) }
            val useCase = CheckAuthStateUseCase(repository)

            assertEquals(AuthCheckResult.Unauthenticated, useCase())
        }
    }

    @Test
    fun invoke_logs_out_on_session_expired() {
        runTest {
            val repository = FakeAuthRepository(
                currentUserResult = AppResult.Error(
                    message = "Сессия истекла",
                    appError = AppError.SessionExpired("Сессия истекла"),
                ),
            ).apply { setToken("expired-token") }
            val useCase = CheckAuthStateUseCase(repository)

            assertEquals(AuthCheckResult.Unauthenticated, useCase())
            assertTrue(repository.logoutCalled)
        }
    }
}
