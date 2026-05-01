package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport

interface ReportRepository {
    suspend fun getStockSummary(warehouseId: Long? = null): AppResult<List<StockSummaryReport>>
    suspend fun getLowStockReport(warehouseId: Long? = null): AppResult<List<LowStockReport>>
    suspend fun getOperationsReport(
        operationType: StockOperationType? = null, productId: Long? = null,
        from: String? = null, to: String? = null, userId: Long? = null,
    ): AppResult<List<OperationReport>>
    suspend fun getStockValueReport(warehouseId: Long? = null): AppResult<List<StockValueReport>>
}
