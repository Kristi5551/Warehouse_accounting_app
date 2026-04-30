package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationType

data class StockHistoryFilter(
    val operationType: StockOperationType? = null,
    val from: String? = null,
    val to: String? = null,
    val userId: Long? = null,
)

interface StockRepository {
    suspend fun getStockBalances(warehouseId: Long?): Result<List<StockBalance>>
    suspend fun getLowStock(warehouseId: Long?): Result<List<StockBalance>>
    suspend fun createReceipt(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        price: Double,
        supplier: String?,
        comment: String?,
    ): Result<Unit>

    suspend fun createIssue(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        reason: String?,
        comment: String?,
    ): Result<Unit>

    suspend fun createWriteOff(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        reason: String?,
        comment: String?,
    ): Result<Unit>

    suspend fun createInventory(
        warehouseId: Long,
        productId: Long,
        actualQuantity: Double,
        comment: String?,
    ): Result<Unit>

    suspend fun getProductHistory(productId: Long, filter: StockHistoryFilter): Result<List<StockOperation>>
}
