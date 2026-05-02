package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.StockStatus

data class StockHistoryFilter(
    val operationType: StockOperationType? = null,
    val from: String? = null,
    val to: String? = null,
    val userId: Long? = null,
)

interface StockRepository {
    suspend fun getStockBalances(
        search: String? = null,
        categoryId: Long? = null,
        status: StockStatus? = null,
    ): AppResult<List<StockBalance>>

    suspend fun getLowStock(): AppResult<List<StockBalance>>
    suspend fun createReceipt(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        price: Double,
        supplier: String?,
        comment: String?,
    ): AppResult<StockOperation>

    suspend fun createIssue(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        reason: String?,
        comment: String?,
    ): AppResult<StockOperation>

    suspend fun createWriteOff(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        reason: String?,
        comment: String?,
    ): AppResult<StockOperation>

    suspend fun createInventory(
        warehouseId: Long,
        productId: Long,
        actualQuantity: Double,
        comment: String?,
    ): AppResult<StockOperation>
    suspend fun getProductHistory(productId: Long, filter: StockHistoryFilter): AppResult<List<StockOperation>>
}
