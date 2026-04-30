package com.example.warehouse_accounting_app.domain.usecase.report

import com.example.warehouse_accounting_app.domain.repository.ReportRepository

class GetLowStockReportUseCase(
    private val repository: ReportRepository,
) {
    suspend operator fun invoke(warehouseId: Long?) = repository.getLowStockReport(warehouseId)
}
