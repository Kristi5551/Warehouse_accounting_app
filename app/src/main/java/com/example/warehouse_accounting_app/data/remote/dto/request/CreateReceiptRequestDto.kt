package com.example.warehouse_accounting_app.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateReceiptRequestDto(
    val warehouseId: Long,
    val productId: Long,
    val quantity: String,
    val price: String,
    val supplier: String? = null,
    val comment: String? = null,
)
