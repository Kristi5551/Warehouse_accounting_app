package com.example.warehouse_accounting_app.presentation.stock.balances

import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockStatus

data class StockBalanceState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val balances: List<StockBalance> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: StockStatus? = null,
    val errorMessage: String? = null,
)

/** Локальный фильтр по поиску для режима «Низкие остатки» (данные уже с сервера). */
val StockBalanceState.filteredForLow: List<StockBalance>
    get() {
        val q = searchQuery.trim()
        if (q.isEmpty()) return balances
        return balances.filter {
            it.productName.contains(q, ignoreCase = true) ||
                it.productArticle.contains(q, ignoreCase = true)
        }
    }
