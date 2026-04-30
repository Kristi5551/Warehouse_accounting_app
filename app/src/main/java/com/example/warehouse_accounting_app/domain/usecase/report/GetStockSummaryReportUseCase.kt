package com.example.warehouse_accounting_app.domain.usecase.report

import com.example.warehouse_accounting_app.domain.repository.ReportRepository

class GetStockSummaryReportUseCase(
    private val repository: ReportRepository,
) {
    suspend operator fun invoke(warehouseId: Long?) = repository.getStockSummary(warehouseId)
}
