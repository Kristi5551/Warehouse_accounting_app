package com.example.warehouse_accounting_app.domain.model.reports

data class StockValueReportItem(
    val productId: Long,
    val productArticle: String,
    val productName: String,
    val quantity: Double,
    val purchasePrice: Double,
    val value: Double,
)
