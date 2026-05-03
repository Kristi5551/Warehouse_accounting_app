package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.connectivityMessage
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.remote.api.StockApi
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateInventoryRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateIssueRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateReceiptRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateWriteOffRequestDto
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockStatus
import com.example.warehouse_accounting_app.domain.repository.StockHistoryFilter
import com.example.warehouse_accounting_app.domain.repository.OperationsFilter
import com.example.warehouse_accounting_app.domain.repository.StockRepository
import java.io.IOException
import java.util.Locale

class StockRepositoryImpl(private val api: StockApi) : StockRepository {

    override suspend fun getStockBalances(
        search: String?,
        categoryId: Long?,
        status: StockStatus?,
    ): AppResult<List<StockBalance>> = try {
        AppResult.Success(api.getStockBalances(search, categoryId, status).map { it.toDomain() })
    } catch (e: ApiException) { logApiException(e, "GET /api/stock/balances"); AppResult.Error(e.message ?: "Ошибка загрузки остатков", e)
    } catch (e: IOException) { logNetworkFailure(e, "GET /api/stock/balances"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "GET /api/stock/balances"); AppResult.Error("Ошибка загрузки остатков", e) }

    override suspend fun getLowStock(): AppResult<List<StockBalance>> = try {
        AppResult.Success(api.getLowStock().map { it.toDomain() })
    } catch (e: ApiException) { logApiException(e, "GET /api/stock/low"); AppResult.Error(e.message ?: "Ошибка", e)
    } catch (e: IOException) { logNetworkFailure(e, "GET /api/stock/low"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "GET /api/stock/low"); AppResult.Error("Ошибка загрузки", e) }

    override suspend fun createReceipt(warehouseId: Long, productId: Long, quantity: Double, price: Double, supplier: String?, comment: String?): AppResult<StockOperation> = try {
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
    } catch (e: ApiException) { logApiException(e, "POST /api/stock/receipt"); AppResult.Error(e.message ?: "Ошибка оформления прихода", e)
    } catch (e: IOException) { logNetworkFailure(e, "POST /api/stock/receipt"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "POST /api/stock/receipt"); AppResult.Error("Ошибка оформления прихода", e) }

    override suspend fun createIssue(warehouseId: Long, productId: Long, quantity: Double, reason: String?, comment: String?): AppResult<StockOperation> = try {
        val op = api.createIssue(
            CreateIssueRequestDto(warehouseId, productId, String.format(Locale.US, "%.3f", quantity), reason, comment),
        )
        AppResult.Success(op.toDomain())
    } catch (e: ApiException) { logApiException(e, "POST /api/stock/issue"); AppResult.Error(e.message ?: "Ошибка оформления расхода", e)
    } catch (e: IOException) { logNetworkFailure(e, "POST /api/stock/issue"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "POST /api/stock/issue"); AppResult.Error("Ошибка оформления расхода", e) }

    override suspend fun createWriteOff(warehouseId: Long, productId: Long, quantity: Double, reason: String?, comment: String?): AppResult<StockOperation> = try {
        val op = api.createWriteOff(
            CreateWriteOffRequestDto(warehouseId, productId, String.format(Locale.US, "%.3f", quantity), reason, comment),
        )
        AppResult.Success(op.toDomain())
    } catch (e: ApiException) { logApiException(e, "POST /api/stock/write-off"); AppResult.Error(e.message ?: "Ошибка оформления списания", e)
    } catch (e: IOException) { logNetworkFailure(e, "POST /api/stock/write-off"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "POST /api/stock/write-off"); AppResult.Error("Ошибка оформления списания", e) }

    override suspend fun createInventory(warehouseId: Long, productId: Long, actualQuantity: Double, comment: String?): AppResult<StockOperation> = try {
        val op = api.createInventory(
            CreateInventoryRequestDto(warehouseId, productId, String.format(Locale.US, "%.3f", actualQuantity), comment),
        )
        AppResult.Success(op.toDomain())
    } catch (e: ApiException) { logApiException(e, "POST /api/stock/inventory"); AppResult.Error(e.message ?: "Ошибка инвентаризации", e)
    } catch (e: IOException) { logNetworkFailure(e, "POST /api/stock/inventory"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "POST /api/stock/inventory"); AppResult.Error("Ошибка инвентаризации", e) }

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
    } catch (e: ApiException) { logApiException(e, "GET /api/operations"); AppResult.Error(e.message ?: "Ошибка загрузки истории", e)
    } catch (e: IOException) { logNetworkFailure(e, "GET /api/operations"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "GET /api/operations"); AppResult.Error("Ошибка загрузки истории", e) }

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
    } catch (e: ApiException) { logApiException(e, "GET /api/stock/products/$productId/history"); AppResult.Error(e.message ?: "Ошибка", e)
    } catch (e: IOException) { logNetworkFailure(e, "GET /api/stock/products/$productId/history"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "GET /api/stock/products/$productId/history"); AppResult.Error("Ошибка загрузки истории", e) }
}
