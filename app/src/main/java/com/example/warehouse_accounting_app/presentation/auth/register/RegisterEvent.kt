package com.example.warehouse_accounting_app.presentation.auth.register

import com.example.warehouse_accounting_app.domain.model.UserRole

sealed class RegisterEvent {
    data class FullNameChanged(val value: String) : RegisterEvent()
    data class EmailChanged(val value: String) : RegisterEvent()
    data class PasswordChanged(val value: String) : RegisterEvent()
    data class RepeatPasswordChanged(val value: String) : RegisterEvent()
    data class RoleChanged(val role: UserRole) : RegisterEvent()
    data object Submit : RegisterEvent()
}
