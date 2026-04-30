package com.example.warehouse_accounting_app.domain.model.reports

data class LowStockReport(
    val productId: Long,
    val productArticle: String,
    val productName: String,
    val warehouseId: Long,
    val warehouseName: String,
    val quantity: Double,
    val minStock: Double,
)
