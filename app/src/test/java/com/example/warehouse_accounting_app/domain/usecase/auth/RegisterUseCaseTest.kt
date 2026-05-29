package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.model.UserStatus
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.testutil.FakeAuthRepository
import com.example.warehouse_accounting_app.testutil.sampleUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterUseCaseTest {

    @Test
    fun invoke_registers_storekeeper_with_normalized_email() {
        runTest {
            val pendingUser = sampleUser(
                email = "new.user@company.ru",
                role = UserRole.STOREKEEPER,
                status = UserStatus.PENDING,
            )
            val repository = FakeAuthRepository(registerResult = AppResult.Success(pendingUser))
            val useCase = RegisterUseCase(repository)

            val result = useCase(
                fullName = " Новый Кладовщик ",
                email = " New.User@Company.RU ",
                password = "pass123",
                repeatPassword = "pass123",
                requestedRole = UserRole.STOREKEEPER,
            )

            assertTrue(result is AppResult.Success)
            assertEquals("new.user@company.ru", repository.lastRegisterEmail)
        }
    }
}
