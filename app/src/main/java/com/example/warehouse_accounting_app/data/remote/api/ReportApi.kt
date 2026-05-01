package com.example.warehouse_accounting_app.data.remote.api

import com.example.warehouse_accounting_app.core.config.AppConfig
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.data.remote.dto.response.ErrorResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.LowStockReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.OperationReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockSummaryReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockValueReportResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class ReportApi(private val client: HttpClient, private val json: Json) {

    suspend fun getStockSummary(warehouseId: Long?): List<StockSummaryReportResponseDto> {
        val url = AppConfig.url("api/reports/stock-summary") + (warehouseId?.let { "?warehouseId=$it" } ?: "")
        val r = client.get(url)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun getLowStockReport(warehouseId: Long?): List<LowStockReportResponseDto> {
        val url = AppConfig.url("api/reports/low-stock") + (warehouseId?.let { "?warehouseId=$it" } ?: "")
        val r = client.get(url)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun getOperationsReport(
        operationType: String?, productId: Long?, from: String?, to: String?, userId: Long?,
    ): List<OperationReportResponseDto> {
        val params = buildList {
            if (operationType != null) add("operationType=$operationType")
            if (productId != null) add("productId=$productId")
            if (from != null) add("from=$from")
            if (to != null) add("to=$to")
            if (userId != null) add("userId=$userId")
        }.joinToString("&")
        val url = AppConfig.url("api/reports/operations") + if (params.isNotEmpty()) "?$params" else ""
        val r = client.get(url)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun getStockValueReport(warehouseId: Long?): List<StockValueReportResponseDto> {
        val url = AppConfig.url("api/reports/stock-value") + (warehouseId?.let { "?warehouseId=$it" } ?: "")
        val r = client.get(url)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    private fun parseError(code: Int, raw: String): ApiException {
        val message = runCatching { json.decodeFromString(ErrorResponseDto.serializer(), raw).message }.getOrDefault(raw)
        return ApiException(statusCode = code, message = message)
    }
}
