package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OperationReportItemResponseDto(
    val productArticle: String,
    val productName: String,
    val quantity: String,
    val price: String? = null,
)
