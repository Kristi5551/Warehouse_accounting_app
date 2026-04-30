package com.example.warehouse_accounting_app.domain.model.reports

data class StockSummaryReport(
    val warehouseId: Long,
    val warehouseName: String,
    val productId: Long,
    val productArticle: String,
    val productName: String,
    val quantity: Double,
    val unit: String,
)
