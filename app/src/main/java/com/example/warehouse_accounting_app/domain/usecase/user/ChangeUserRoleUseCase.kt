package com.example.warehouse_accounting_app.domain.usecase.user

import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.UserRepository

class ChangeUserRoleUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(id: Long, role: UserRole) = repository.changeRole(id, role)
}
