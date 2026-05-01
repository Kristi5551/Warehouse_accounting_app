package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class StockOperationItemResponseDto(
    val id: Long,
    val operationId: Long,
    val productId: Long,
    val quantity: String,
    val price: String? = null,
    val reason: String? = null,
)
