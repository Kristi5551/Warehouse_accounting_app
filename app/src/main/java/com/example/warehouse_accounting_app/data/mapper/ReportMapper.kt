package com.example.warehouse_accounting_app.data.mapper

import com.example.warehouse_accounting_app.data.remote.dto.response.LowStockReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.OperationReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.OperationsReportBundleResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockSummaryReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockValueItemResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockValueReportResponseDto
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationReportLine
import com.example.warehouse_accounting_app.domain.model.reports.OperationsReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReportItem

fun StockSummaryReportResponseDto.toDomain(): StockSummaryReport =
    StockSummaryReport(
        totalProducts = totalProducts,
        inStockCount = inStockCount,
        lowStockCount = lowStockCount,
        outOfStockCount = outOfStockCount,
        balances = balances.map { it.toDomain() },
    )

fun LowStockReportResponseDto.toDomain(): LowStockReport =
    LowStockReport(
        productId = productId,
        productArticle = productArticle,
        productName = productName,
        warehouseId = warehouseId,
        warehouseName = warehouseName,
        quantity = quantity.toDoubleOrNull() ?: 0.0,
        minStock = minStock.toDoubleOrNull() ?: 0.0,
    )

fun StockValueReportResponseDto.toDomain(): StockValueReport =
    StockValueReport(
        totalValue = totalValue.toDoubleOrNull() ?: 0.0,
        items = items.map { it.toDomain() },
    )

fun StockValueItemResponseDto.toDomain(): StockValueReportItem =
    StockValueReportItem(
        productId = productId,
        productArticle = productArticle,
        productName = productName,
        quantity = quantity.toDoubleOrNull() ?: 0.0,
        purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
        value = value.toDoubleOrNull() ?: 0.0,
    )

fun OperationReportResponseDto.toOperationLine(): OperationReportLine =
    OperationReportLine(
        operationId = operationId,
        operationType = runCatching { StockOperationType.valueOf(operationType) }.getOrDefault(StockOperationType.RECEIPT),
        warehouseId = warehouseId,
        warehouseName = warehouseName,
        createdByName = createdByName,
        createdAt = createdAt,
        productArticle = productArticle,
        productName = productName,
        quantity = quantity.toDoubleOrNull() ?: 0.0,
        price = price?.toDoubleOrNull(),
    )

fun OperationsReportBundleResponseDto.toDomain(): OperationsReport =
    OperationsReport(
        operations = operations.map { it.toOperationLine() },
        receiptCount = receiptCount,
        issueCount = issueCount,
        writeOffCount = writeOffCount,
        inventoryCount = inventoryCount,
    )
