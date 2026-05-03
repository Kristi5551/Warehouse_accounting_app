package com.example.warehouse_accounting_app.domain.model.reports

import com.example.warehouse_accounting_app.domain.model.StockOperationType

data class OperationReportLine(
    val operationId: Long,
    val operationType: StockOperationType,
    val warehouseId: Long,
    val warehouseName: String,
    val createdByName: String,
    val createdAt: String,
    val productArticle: String,
    val productName: String,
    val quantity: Double,
    val price: Double?,
    /** Все позиции операции (как минимум одна; дубли операции в списке отчёта исключены). */
    val items: List<OperationReportItemLine> = emptyList(),
)
