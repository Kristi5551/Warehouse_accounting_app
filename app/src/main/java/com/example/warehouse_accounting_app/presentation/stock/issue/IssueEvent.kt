package com.example.warehouse_accounting_app.presentation.stock.issue

sealed interface IssueEvent {
    data object Success : IssueEvent
    data class Error(val message: String) : IssueEvent
}
