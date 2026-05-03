package com.example.warehouse_accounting_app.presentation.reports

sealed interface ReportsEvent {
    data object RefreshAll : ReportsEvent
    data class DateFromChanged(val value: String) : ReportsEvent
    data class DateToChanged(val value: String) : ReportsEvent
    data object ApplyOperationsPeriod : ReportsEvent
}
