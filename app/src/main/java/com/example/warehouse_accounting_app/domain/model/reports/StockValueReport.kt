package com.example.warehouse_accounting_app.domain.model.reports

data class StockValueReport(
    val warehouseId: Long,
    val warehouseName: String,
    val totalPurchaseValue: Double,
    val totalSaleValue: Double,
)
