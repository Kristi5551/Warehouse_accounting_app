package com.example.warehouse_accounting_app.presentation.stock.receipt

sealed interface ReceiptEvent {
    data object Success : ReceiptEvent
    data class Error(val message: String) : ReceiptEvent
}
