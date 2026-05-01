package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class StockBalanceResponseDto(
    val id: Long,
    val productId: Long,
    val productArticle: String,
    val productName: String,
    val categoryName: String? = null,
    val warehouseId: Long,
    val warehouseName: String,
    val quantity: String,
    val minStock: String,
    val status: String,
    val updatedAt: String,
)
