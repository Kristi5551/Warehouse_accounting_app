package com.example.warehouse_accounting_app.domain.model.reports

data class StockValueReport(
    val totalValue: Double,
    val items: List<StockValueReportItem>,
)
