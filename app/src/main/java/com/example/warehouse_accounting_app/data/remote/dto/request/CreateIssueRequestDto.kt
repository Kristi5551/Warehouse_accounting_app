package com.example.warehouse_accounting_app.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateIssueRequestDto(
    val warehouseId: Long,
    val productId: Long,
    val quantity: String,
    val reason: String? = null,
    val comment: String? = null,
)
