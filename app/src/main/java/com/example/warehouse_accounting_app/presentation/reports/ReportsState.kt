package com.example.warehouse_accounting_app.presentation.reports

import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationsReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport

data class ReportsState(
    val isLoading: Boolean = false,
    val stockSummary: StockSummaryReport? = null,
    val lowStockReport: List<LowStockReport> = emptyList(),
    val operationsReport: OperationsReport? = null,
    val stockValueReport: StockValueReport? = null,
    val dateFromInput: String = "",
    val dateToInput: String = "",
    val errorMessage: String? = null,
)
