package com.example.warehouse_accounting_app.presentation.stock.inventory

sealed interface InventoryEvent {
    data object Success : InventoryEvent
    data class Error(val message: String) : InventoryEvent
}
