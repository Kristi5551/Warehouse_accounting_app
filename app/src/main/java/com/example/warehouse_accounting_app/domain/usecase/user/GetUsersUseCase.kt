package com.example.warehouse_accounting_app.domain.usecase.user

import com.example.warehouse_accounting_app.domain.repository.UserRepository

class GetUsersUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke() = repository.getUsers()
}
