package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class StockSummaryReportResponseDto(
    val totalProducts: Int,
    val inStockCount: Int,
    val lowStockCount: Int,
    val outOfStockCount: Int,
    val balances: List<StockBalanceResponseDto> = emptyList(),
)
