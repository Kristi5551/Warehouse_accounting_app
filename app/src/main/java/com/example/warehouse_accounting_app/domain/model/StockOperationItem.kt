package com.example.warehouse_accounting_app.domain.model

/**
 * Строка складской операции.
 *
 * [quantity] > 0 — величина; направление задаёт тип операции ([StockOperation.operationType]).
 */
data class StockOperationItem(
    val id: Long,
    val operationId: Long,
    val productId: Long,
    val productArticle: String?,
    val productName: String?,
    val quantity: Double,
    val price: Double?,
    val reason: String?,
)
