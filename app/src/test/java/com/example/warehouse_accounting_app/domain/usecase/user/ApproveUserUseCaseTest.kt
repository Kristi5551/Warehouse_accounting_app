package com.example.warehouse_accounting_app.domain.usecase.user

import com.example.warehouse_accounting_app.domain.model.UserStatus
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.testutil.FakeUserRepository
import com.example.warehouse_accounting_app.testutil.sampleUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ApproveUserUseCaseTest {

    @Test
    fun invoke_approves_pending_user() {
        runTest {
            val approved = sampleUser(id = 5L, status = UserStatus.ACTIVE)
            val repository = FakeUserRepository(approveResult = AppResult.Success(approved))
            val useCase = ApproveUserUseCase(repository)

            val result = useCase(5L)

            assertTrue(result is AppResult.Success)
            assertEquals(5L, repository.lastApprovedId)
        }
    }
}
