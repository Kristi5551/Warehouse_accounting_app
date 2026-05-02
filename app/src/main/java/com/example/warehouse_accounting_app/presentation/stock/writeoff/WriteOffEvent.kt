package com.example.warehouse_accounting_app.presentation.stock.writeoff

sealed interface WriteOffEvent {
    data object Success : WriteOffEvent
    data class Error(val message: String) : WriteOffEvent
}
