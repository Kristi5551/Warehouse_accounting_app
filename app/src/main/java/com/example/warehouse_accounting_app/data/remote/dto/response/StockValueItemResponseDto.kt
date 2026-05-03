package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class StockValueItemResponseDto(
    val productId: Long,
    val productArticle: String,
    val productName: String,
    val quantity: String,
    val purchasePrice: String,
    val value: String,
)
