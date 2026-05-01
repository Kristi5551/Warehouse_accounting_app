package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.connectivityMessage
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.remote.api.ReportApi
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport
import com.example.warehouse_accounting_app.domain.repository.ReportRepository
import java.io.IOException

class ReportRepositoryImpl(private val api: ReportApi) : ReportRepository {

    override suspend fun getStockSummary(warehouseId: Long?): AppResult<List<StockSummaryReport>> = try {
        AppResult.Success(api.getStockSummary(warehouseId).map { it.toDomain() })
    } catch (e: ApiException) { logApiException(e, "GET /api/reports/stock-summary"); AppResult.Error(e.message ?: "Ошибка", e)
    } catch (e: IOException) { logNetworkFailure(e, "GET /api/reports/stock-summary"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "GET /api/reports/stock-summary"); AppResult.Error("Ошибка загрузки отчёта", e) }

    override suspend fun getLowStockReport(warehouseId: Long?): AppResult<List<LowStockReport>> = try {
        AppResult.Success(api.getLowStockReport(warehouseId).map { it.toDomain() })
    } catch (e: ApiException) { logApiException(e, "GET /api/reports/low-stock"); AppResult.Error(e.message ?: "Ошибка", e)
    } catch (e: IOException) { logNetworkFailure(e, "GET /api/reports/low-stock"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "GET /api/reports/low-stock"); AppResult.Error("Ошибка загрузки", e) }

    override suspend fun getOperationsReport(
        operationType: StockOperationType?, productId: Long?, from: String?, to: String?, userId: Long?,
    ): AppResult<List<OperationReport>> = try {
        AppResult.Success(api.getOperationsReport(operationType?.name, productId, from, to, userId).map { it.toDomain() })
    } catch (e: ApiException) { logApiException(e, "GET /api/reports/operations"); AppResult.Error(e.message ?: "Ошибка", e)
    } catch (e: IOException) { logNetworkFailure(e, "GET /api/reports/operations"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "GET /api/reports/operations"); AppResult.Error("Ошибка загрузки", e) }

    override suspend fun getStockValueReport(warehouseId: Long?): AppResult<List<StockValueReport>> = try {
        AppResult.Success(api.getStockValueReport(warehouseId).map { it.toDomain() })
    } catch (e: ApiException) { logApiException(e, "GET /api/reports/stock-value"); AppResult.Error(e.message ?: "Ошибка", e)
    } catch (e: IOException) { logNetworkFailure(e, "GET /api/reports/stock-value"); AppResult.Error(connectivityMessage(e), e)
    } catch (e: Exception) { logNetworkFailure(e, "GET /api/reports/stock-value"); AppResult.Error("Ошибка загрузки", e) }
}
