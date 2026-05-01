package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class StockOperationResponseDto(
    val id: Long,
    val operationType: String,
    val warehouseId: Long,
    val warehouseName: String? = null,
    val createdBy: Long,
    val createdByName: String? = null,
    val createdAt: String,
    val comment: String? = null,
    val items: List<StockOperationItemResponseDto> = emptyList(),
)
