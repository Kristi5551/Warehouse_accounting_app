package com.example.warehouse_accounting_app.domain.usecase.user

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.UserRepository

class ChangeUserRoleUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(id: Long, role: UserRole): AppResult<User> {
        if (id <= 0) return AppResult.Error("Некорректный идентификатор")
        return repository.changeUserRole(id, role)
    }
}
