package com.example.warehouse_accounting_app.data.mapper

import com.example.warehouse_accounting_app.data.remote.dto.response.LowStockReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.OperationReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockSummaryReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockValueReportResponseDto
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport

fun StockSummaryReportResponseDto.toDomain(): StockSummaryReport = StockSummaryReport(
    warehouseId = warehouseId, warehouseName = warehouseName,
    productId = productId, productArticle = productArticle, productName = productName,
    quantity = quantity.toDoubleOrNull() ?: 0.0, unit = unit,
)

fun LowStockReportResponseDto.toDomain(): LowStockReport = LowStockReport(
    productId = productId, productArticle = productArticle, productName = productName,
    warehouseId = warehouseId, warehouseName = warehouseName,
    quantity = quantity.toDoubleOrNull() ?: 0.0, minStock = minStock.toDoubleOrNull() ?: 0.0,
)

fun StockValueReportResponseDto.toDomain(): StockValueReport = StockValueReport(
    warehouseId = warehouseId, warehouseName = warehouseName,
    totalPurchaseValue = totalPurchaseValue.toDoubleOrNull() ?: 0.0,
    totalSaleValue = totalSaleValue.toDoubleOrNull() ?: 0.0,
)

fun OperationReportResponseDto.toDomain(): OperationReport = OperationReport(
    operationId = operationId,
    operationType = runCatching { StockOperationType.valueOf(operationType) }.getOrDefault(StockOperationType.RECEIPT),
    warehouseId = warehouseId, warehouseName = warehouseName,
    createdByName = createdByName, createdAt = createdAt,
    productArticle = productArticle, productName = productName,
    quantity = quantity.toDoubleOrNull() ?: 0.0,
    price = price?.toDoubleOrNull(),
)
