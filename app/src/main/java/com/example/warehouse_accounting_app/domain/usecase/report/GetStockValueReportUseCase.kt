package com.example.warehouse_accounting_app.domain.usecase.report

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport
import com.example.warehouse_accounting_app.domain.repository.ReportRepository

class GetStockValueReportUseCase(private val repository: ReportRepository) {
    suspend operator fun invoke(warehouseId: Long? = null): AppResult<StockValueReport> =
        repository.getStockValueReport(warehouseId)
}
