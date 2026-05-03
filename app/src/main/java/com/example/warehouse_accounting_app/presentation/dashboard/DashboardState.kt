package com.example.warehouse_accounting_app.presentation.dashboard

import com.example.warehouse_accounting_app.domain.model.User

data class DashboardState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    /** Сессия истекла (401/403 на /api/auth/me) — нужно выйти и перейти на Login. */
    val sessionExpired: Boolean = false,
)
