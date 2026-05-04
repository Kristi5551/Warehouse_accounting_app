package com.example.warehouse_accounting_app.domain.usecase.user

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.repository.UserRepository

class UnblockUserUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(id: Long): AppResult<User> {
        if (id <= 0) return AppResult.Error("Некорректный идентификатор")
        return repository.unblockUser(id)
    }
}
