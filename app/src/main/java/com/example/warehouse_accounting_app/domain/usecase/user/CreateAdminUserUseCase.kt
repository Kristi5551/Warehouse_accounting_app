package com.example.warehouse_accounting_app.domain.usecase.user

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.repository.UserRepository

class CreateAdminUserUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(fullName: String, email: String, password: String): AppResult<User> =
        userRepository.createAdmin(fullName, email, password)
}
