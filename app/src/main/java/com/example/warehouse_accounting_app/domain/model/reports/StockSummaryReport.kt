package com.example.warehouse_accounting_app.domain.model.reports

import com.example.warehouse_accounting_app.domain.model.StockBalance

data class StockSummaryReport(
    val totalProducts: Int,
    val inStockCount: Int,
    val lowStockCount: Int,
    val outOfStockCount: Int,
    val balances: List<StockBalance>,
)
