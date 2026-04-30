package com.example.warehouse_accounting_app.presentation.auth.login

sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data object Submit : LoginEvent
    data object RegisterNavigation : LoginEvent
}
