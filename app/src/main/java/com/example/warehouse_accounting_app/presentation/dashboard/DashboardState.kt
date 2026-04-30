package com.example.warehouse_accounting_app.presentation.dashboard

import com.example.warehouse_accounting_app.domain.model.User

data class DashboardState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
