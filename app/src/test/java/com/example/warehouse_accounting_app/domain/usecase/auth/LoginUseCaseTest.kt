package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.testutil.FakeAuthRepository
import com.example.warehouse_accounting_app.testutil.sampleUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUseCaseTest {

    @Test
    fun invoke_authenticates_user_through_repository() {
        runTest {
            val user = sampleUser(email = "admin@warehouse.local", role = UserRole.ADMIN)
            val repository = FakeAuthRepository(loginResult = AppResult.Success(user))
            val useCase = LoginUseCase(repository)

            val result = useCase(" admin@warehouse.local ", "secret123")

            assertTrue(result is AppResult.Success)
            assertEquals(user, (result as AppResult.Success).data)
            assertEquals("admin@warehouse.local", repository.lastLoginEmail)
        }
    }
}
