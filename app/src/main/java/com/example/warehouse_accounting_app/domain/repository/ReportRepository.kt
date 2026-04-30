package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport

interface ReportRepository {
    suspend fun getStockSummary(warehouseId: Long?): Result<List<StockSummaryReport>>
    suspend fun getLowStockReport(warehouseId: Long?): Result<List<LowStockReport>>
    suspend fun getOperationsReport(
        operationType: StockOperationType?,
        productId: Long?,
        from: String?,
        to: String?,
        userId: Long?,
    ): Result<List<OperationReport>>

    suspend fun getStockValueReport(warehouseId: Long?): Result<List<StockValueReport>>
}
