package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationsReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport

interface ReportRepository {
    suspend fun getStockSummary(warehouseId: Long? = null): AppResult<StockSummaryReport>
    suspend fun getLowStockReport(warehouseId: Long? = null): AppResult<List<LowStockReport>>
    suspend fun getOperationsReport(dateFrom: String?, dateTo: String?): AppResult<OperationsReport>
    suspend fun getStockValueReport(warehouseId: Long? = null): AppResult<StockValueReport>
}
