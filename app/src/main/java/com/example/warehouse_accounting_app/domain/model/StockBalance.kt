package com.example.warehouse_accounting_app.domain.model

data class StockBalance(
    val id: Long,
    val productId: Long,
    val productArticle: String,
    val productName: String,
    val categoryName: String?,
    val warehouseId: Long,
    val warehouseName: String,
    val quantity: Double,
    val minStock: Double,
    val status: StockStatus,
    val updatedAt: String,
)
