package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.data.mapper.apiFailure
import com.example.warehouse_accounting_app.data.mapper.networkFailure
import com.example.warehouse_accounting_app.data.mapper.unknownFailure
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.remote.api.ReportApi
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationsReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport
import com.example.warehouse_accounting_app.domain.repository.ReportRepository
import com.example.warehouse_accounting_app.domain.result.AppResult
import java.io.IOException

class ReportRepositoryImpl(private val api: ReportApi) : ReportRepository {

    override suspend fun getStockSummary(warehouseId: Long?): AppResult<StockSummaryReport> = try {
        AppResult.Success(api.getStockSummary(warehouseId).toDomain())
    } catch (e: ApiException) {
        logApiException(e, "GET /api/reports/stock-summary")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "GET /api/reports/stock-summary")
        networkFailure(e, "Ошибка загрузки отчёта")
    } catch (e: Exception) {
        logNetworkFailure(e, "GET /api/reports/stock-summary")
        unknownFailure(e, "Ошибка загрузки отчёта")
    }

    override suspend fun getLowStockReport(warehouseId: Long?): AppResult<List<LowStockReport>> = try {
        AppResult.Success(api.getLowStockReport(warehouseId).map { it.toDomain() })
    } catch (e: ApiException) {
        logApiException(e, "GET /api/reports/low-stock")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "GET /api/reports/low-stock")
        networkFailure(e, "Ошибка загрузки")
    } catch (e: Exception) {
        logNetworkFailure(e, "GET /api/reports/low-stock")
        unknownFailure(e, "Ошибка загрузки")
    }

    override suspend fun getOperationsReport(dateFrom: String?, dateTo: String?): AppResult<OperationsReport> = try {
        AppResult.Success(api.getOperationsReport(dateFrom, dateTo).toDomain())
    } catch (e: ApiException) {
        logApiException(e, "GET /api/reports/operations")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "GET /api/reports/operations")
        networkFailure(e, "Ошибка загрузки")
    } catch (e: Exception) {
        logNetworkFailure(e, "GET /api/reports/operations")
        unknownFailure(e, "Ошибка загрузки")
    }

    override suspend fun getStockValueReport(warehouseId: Long?): AppResult<StockValueReport> = try {
        AppResult.Success(api.getStockValueReport(warehouseId).toDomain())
    } catch (e: ApiException) {
        logApiException(e, "GET /api/reports/stock-value")
        apiFailure(e)
    } catch (e: IOException) {
        logNetworkFailure(e, "GET /api/reports/stock-value")
        networkFailure(e, "Ошибка загрузки")
    } catch (e: Exception) {
        logNetworkFailure(e, "GET /api/reports/stock-value")
        unknownFailure(e, "Ошибка загрузки")
    }
}
