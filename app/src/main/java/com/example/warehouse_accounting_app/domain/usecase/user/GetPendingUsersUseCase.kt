package com.example.warehouse_accounting_app.domain.usecase.user

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.repository.UserRepository

class GetPendingUsersUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(): AppResult<List<User>> = repository.getPendingUsers()
}
