package com.example.warehouse_accounting_app.domain.usecase.report

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.reports.OperationReport
import com.example.warehouse_accounting_app.domain.repository.ReportRepository

class GetOperationsReportUseCase(private val repository: ReportRepository) {
    suspend operator fun invoke(
        operationType: StockOperationType?,
        productId: Long?,
        from: String?,
        to: String?,
        userId: Long?,
    ): AppResult<List<OperationReport>> =
        repository.getOperationsReport(operationType, productId, from, to, userId)
}
