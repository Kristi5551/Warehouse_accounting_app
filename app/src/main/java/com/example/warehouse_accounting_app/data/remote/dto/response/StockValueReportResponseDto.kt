package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class StockValueReportResponseDto(
    val totalValue: String,
    val items: List<StockValueItemResponseDto> = emptyList(),
)
