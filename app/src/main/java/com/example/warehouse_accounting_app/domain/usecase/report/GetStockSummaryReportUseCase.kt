package com.example.warehouse_accounting_app.domain.usecase.report

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.repository.ReportRepository

class GetStockSummaryReportUseCase(private val repository: ReportRepository) {
    suspend operator fun invoke(warehouseId: Long? = null): AppResult<StockSummaryReport> =
        repository.getStockSummary(warehouseId)
}
