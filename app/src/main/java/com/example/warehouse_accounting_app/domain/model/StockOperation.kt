package com.example.warehouse_accounting_app.domain.model

data class StockOperation(
    val id: Long,
    val operationType: StockOperationType,
    val warehouseId: Long,
    val warehouseName: String?,
    val createdBy: Long,
    val createdByName: String?,
    val createdAt: String,
    val comment: String?,
    val items: List<StockOperationItem>,
)
