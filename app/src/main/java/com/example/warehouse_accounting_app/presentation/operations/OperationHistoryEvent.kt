package com.example.warehouse_accounting_app.presentation.operations

import com.example.warehouse_accounting_app.domain.model.StockOperationType

sealed interface OperationHistoryEvent {
    data object Refresh : OperationHistoryEvent
    data class TypeFilterChanged(val type: StockOperationType?) : OperationHistoryEvent
    data class ProductFilterChanged(val productId: Long?) : OperationHistoryEvent
    data class UserFilterChanged(val userId: Long?) : OperationHistoryEvent
    data class DateFromChanged(val value: String) : OperationHistoryEvent
    data class DateToChanged(val value: String) : OperationHistoryEvent
    data object ApplyFilters : OperationHistoryEvent
}
