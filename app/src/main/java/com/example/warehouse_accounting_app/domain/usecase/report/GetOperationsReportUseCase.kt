package com.example.warehouse_accounting_app.domain.usecase.report

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.reports.OperationsReport
import com.example.warehouse_accounting_app.domain.repository.ReportRepository

class GetOperationsReportUseCase(private val repository: ReportRepository) {
    suspend operator fun invoke(dateFrom: String?, dateTo: String?): AppResult<OperationsReport> =
        repository.getOperationsReport(dateFrom, dateTo)
}
