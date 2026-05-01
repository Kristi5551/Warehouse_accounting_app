package com.example.warehouse_accounting_app.data.mapper

import com.example.warehouse_accounting_app.data.remote.dto.response.StockBalanceResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockOperationItemResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockOperationResponseDto
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationItem
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.StockStatus

fun StockBalanceResponseDto.toDomain(): StockBalance = StockBalance(
    id = id,
    productId = productId,
    productArticle = productArticle,
    productName = productName,
    categoryName = categoryName,
    warehouseId = warehouseId,
    warehouseName = warehouseName,
    quantity = quantity.toDoubleOrNull() ?: 0.0,
    minStock = minStock.toDoubleOrNull() ?: 0.0,
    status = runCatching { StockStatus.valueOf(status) }.getOrDefault(StockStatus.IN_STOCK),
    updatedAt = updatedAt,
)

fun StockOperationItemResponseDto.toDomain(): StockOperationItem = StockOperationItem(
    id = id,
    operationId = operationId,
    productId = productId,
    productName = null,
    quantity = quantity.toDoubleOrNull() ?: 0.0,
    price = price?.toDoubleOrNull(),
    reason = reason,
)

fun StockOperationResponseDto.toDomain(): StockOperation = StockOperation(
    id = id,
    operationType = runCatching { StockOperationType.valueOf(operationType) }.getOrDefault(StockOperationType.RECEIPT),
    warehouseId = warehouseId,
    warehouseName = warehouseName,
    createdBy = createdBy,
    createdByName = createdByName,
    createdAt = createdAt,
    comment = comment,
    items = items.map { it.toDomain() },
)
