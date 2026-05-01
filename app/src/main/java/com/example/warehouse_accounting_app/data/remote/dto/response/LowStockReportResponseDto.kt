package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LowStockReportResponseDto(
    val productId: Long,
    val productArticle: String,
    val productName: String,
    val warehouseId: Long,
    val warehouseName: String,
    val quantity: String,
    val minStock: String,
)
