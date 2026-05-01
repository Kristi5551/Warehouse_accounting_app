package com.example.warehouse_accounting_app.presentation.stock.balances

import com.example.warehouse_accounting_app.domain.model.StockStatus

sealed interface StockBalanceEvent {
    data class SearchChanged(val value: String) : StockBalanceEvent
    data class StatusFilterChanged(val status: StockStatus?) : StockBalanceEvent
    data object Refresh : StockBalanceEvent
}
