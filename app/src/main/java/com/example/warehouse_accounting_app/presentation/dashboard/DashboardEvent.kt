package com.example.warehouse_accounting_app.presentation.dashboard

sealed interface DashboardEvent {
    data object Logout : DashboardEvent
    data class Navigate(val route: String) : DashboardEvent
}
