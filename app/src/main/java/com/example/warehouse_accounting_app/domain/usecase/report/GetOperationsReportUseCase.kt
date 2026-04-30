package com.example.warehouse_accounting_app.domain.usecase.report

import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.repository.ReportRepository

class GetOperationsReportUseCase(
    private val repository: ReportRepository,
) {
    suspend operator fun invoke(
        operationType: StockOperationType?,
        productId: Long?,
        from: String?,
        to: String?,
        userId: Long?,
    ) = repository.getOperationsReport(operationType, productId, from, to, userId)
}
