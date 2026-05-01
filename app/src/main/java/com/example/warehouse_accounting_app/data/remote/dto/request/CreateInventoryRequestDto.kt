package com.example.warehouse_accounting_app.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateInventoryRequestDto(
    val warehouseId: Long,
    val productId: Long,
    val actualQuantity: String,
    val comment: String? = null,
)
