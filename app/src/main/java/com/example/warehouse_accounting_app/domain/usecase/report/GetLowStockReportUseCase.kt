package com.example.warehouse_accounting_app.domain.usecase.report

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.repository.ReportRepository

/** Данные с `GET /api/reports/low-stock` для экрана отчётов (строки [LowStockReport]). */
class GetLowStockReportUseCase(private val repository: ReportRepository) {
    suspend operator fun invoke(warehouseId: Long?): AppResult<List<LowStockReport>> =
        repository.getLowStockReport(warehouseId)
}
