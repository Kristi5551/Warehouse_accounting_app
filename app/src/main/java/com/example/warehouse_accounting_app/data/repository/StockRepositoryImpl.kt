package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.data.mapper.apiFailure
import com.example.warehouse_accounting_app.data.mapper.networkFailure
import com.example.warehouse_accounting_app.data.mapper.unknownFailure
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.remote.api.StockApi
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateInventoryRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateIssueRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateReceiptRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateWriteOffRequestDto
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockStatus
import com.example.warehouse_accounting_app.domain.repository.OperationsFilter
import com.example.warehouse_accounting_app.domain.repository.StockHistoryFilter
import com.example.warehouse_accounting_app.domain.repository.StockRepository
import com.example.warehouse_accounting_app.domain.result.AppResult
import java.io.IOException
import java.util.Locale

class StockRepositoryImpl(private val api: StockApi) : StockRepository {

    override suspend fun getStockBalances(
        search: String?,
        categoryId: Long?,
        status: StockStatus?,
    ): AppResult<List<StockBalance>> = try {
        AppResult.Success(api.getStockBalances(search, categoryId, status).map { it.toDomain() })
    } catch (e: ApiException) {
        logApiException(e, "GET /api/stock/balances")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "GET /api/stock/balances")
        networkFailure(e, "Ошибка загрузки остатков")
    } catch (e: Exception) {
        logNetworkFailure(e, "GET /api/stock/balances")
        unknownFailure(e, "Ошибка загрузки остатков")
    }

    override suspend fun getLowStock(): AppResult<List<StockBalance>> = try {
        AppResult.Success(api.getLowStock().map { it.toDomain() })
    } catch (e: ApiException) {
        logApiException(e, "GET /api/stock/low")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "GET /api/stock/low")
        networkFailure(e, "Ошибка загрузки")
    } catch (e: Exception) {
        logNetworkFailure(e, "GET /api/stock/low")
        unknownFailure(e, "Ошибка загрузки")
    }

    override suspend fun createReceipt(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        price: Double,
        supplier: String?,
        comment: String?,
    ): AppResult<StockOperation> = try {
        val op = api.createReceipt(
            CreateReceiptRequestDto(
                warehouseId,
                productId,
                String.format(Locale.US, "%.3f", quantity),
                String.format(Locale.US, "%.2f", price),
                supplier,
                comment,
            ),
        )
        AppResult.Success(op.toDomain())
    } catch (e: ApiException) {
        logApiException(e, "POST /api/stock/receipt")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "POST /api/stock/receipt")
        networkFailure(e, "Ошибка оформления прихода")
    } catch (e: Exception) {
        logNetworkFailure(e, "POST /api/stock/receipt")
        unknownFailure(e, "Ошибка оформления прихода")
    }

    override suspend fun createIssue(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        reason: String?,
        comment: String?,
    ): AppResult<StockOperation> = try {
        val op = api.createIssue(
            CreateIssueRequestDto(
                warehouseId,
                productId,
                String.format(Locale.US, "%.3f", quantity),
                reason,
                comment,
            ),
        )
        AppResult.Success(op.toDomain())
    } catch (e: ApiException) {
        logApiException(e, "POST /api/stock/issue")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "POST /api/stock/issue")
        networkFailure(e, "Ошибка оформления расхода")
    } catch (e: Exception) {
        logNetworkFailure(e, "POST /api/stock/issue")
        unknownFailure(e, "Ошибка оформления расхода")
    }

    override suspend fun createWriteOff(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        reason: String?,
        comment: String?,
    ): AppResult<StockOperation> = try {
        val op = api.createWriteOff(
            CreateWriteOffRequestDto(
                warehouseId,
                productId,
                String.format(Locale.US, "%.3f", quantity),
                reason,
                comment,
            ),
        )
        AppResult.Success(op.toDomain())
    } catch (e: ApiException) {
        logApiException(e, "POST /api/stock/write-off")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "POST /api/stock/write-off")
        networkFailure(e, "Ошибка оформления списания")
    } catch (e: Exception) {
        logNetworkFailure(e, "POST /api/stock/write-off")
        unknownFailure(e, "Ошибка оформления списания")
    }

    override suspend fun createInventory(
        warehouseId: Long,
        productId: Long,
        actualQuantity: Double,
        comment: String?,
    ): AppResult<StockOperation> = try {
        val op = api.createInventory(
            CreateInventoryRequestDto(
                warehouseId,
                productId,
                String.format(Locale.US, "%.3f", actualQuantity),
                comment,
            ),
        )
        AppResult.Success(op.toDomain())
    } catch (e: ApiException) {
        logApiException(e, "POST /api/stock/inventory")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "POST /api/stock/inventory")
        networkFailure(e, "Ошибка инвентаризации")
    } catch (e: Exception) {
        logNetworkFailure(e, "POST /api/stock/inventory")
        unknownFailure(e, "Ошибка инвентаризации")
    }

    override suspend fun getOperations(filter: OperationsFilter): AppResult<List<StockOperation>> = try {
        AppResult.Success(
            api.getOperations(
                filter.type?.name,
                filter.productId,
                filter.userId,
                filter.dateFrom,
                filter.dateTo,
            ).map { it.toDomain() },
        )
    } catch (e: ApiException) {
        logApiException(e, "GET /api/operations")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "GET /api/operations")
        networkFailure(e, "Ошибка загрузки истории")
    } catch (e: Exception) {
        logNetworkFailure(e, "GET /api/operations")
        unknownFailure(e, "Ошибка загрузки истории")
    }

    override suspend fun getProductHistory(productId: Long, filter: StockHistoryFilter): AppResult<List<StockOperation>> = try {
        AppResult.Success(
            api.getProductHistory(
                productId,
                filter.operationType?.name,
                filter.userId,
                filter.dateFrom,
                filter.dateTo,
            ).map { it.toDomain() },
        )
    } catch (e: ApiException) {
        logApiException(e, "GET /api/stock/products/$productId/history")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "GET /api/stock/products/$productId/history")
        networkFailure(e, "Ошибка загрузки истории")
    } catch (e: Exception) {
        logNetworkFailure(e, "GET /api/stock/products/$productId/history")
        unknownFailure(e, "Ошибка загрузки истории")
    }
}
