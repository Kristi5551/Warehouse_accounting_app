package com.example.warehouse_accounting_app.presentation.auth.register

import com.example.warehouse_accounting_app.domain.model.UserRole

data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val selectedRole: UserRole = UserRole.STOREKEEPER,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
