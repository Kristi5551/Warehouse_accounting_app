package com.example.warehouse_accounting_app.domain.model

data class StockOperationItem(
    val id: Long,
    val operationId: Long,
    val productId: Long,
    val productName: String?,
    val quantity: Double,
    val price: Double?,
    val reason: String?,
)
