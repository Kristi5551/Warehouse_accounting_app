package com.example.warehouse_accounting_app.data.remote.api

import com.example.warehouse_accounting_app.core.config.AppConfig
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.data.remote.dto.response.ErrorResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.LowStockReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.OperationsReportBundleResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockSummaryReportResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockValueReportResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ReportApi(private val client: HttpClient, private val json: Json) {

    private fun enc(s: String) = URLEncoder.encode(s, StandardCharsets.UTF_8)

    suspend fun getStockSummary(warehouseId: Long?): StockSummaryReportResponseDto {
        val url = AppConfig.url("api/reports/stock-summary") + (warehouseId?.let { "?warehouseId=$it" } ?: "")
        val r = client.get(url)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    /** Отчётные строки по низким остаткам: `GET /api/reports/low-stock` → [LowStockReportResponseDto]. */
    suspend fun getLowStockReport(warehouseId: Long?): List<LowStockReportResponseDto> {
        val url = AppConfig.url("api/reports/low-stock") + (warehouseId?.let { "?warehouseId=$it" } ?: "")
        val r = client.get(url)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    /**
     * `GET /api/reports/operations` — **dateFrom** / **dateTo** в `yyyy-MM-dd` или не передаются, если null/пусто.
     * На сервере: день **dateTo** включается целиком (`API_DATE_RANGE.md` в модуле сервера).
     */
    suspend fun getOperationsReport(dateFrom: String?, dateTo: String?): OperationsReportBundleResponseDto {
        val parts = buildList {
            dateFrom?.takeIf { it.isNotBlank() }?.let { add("dateFrom=${enc(it.trim())}") }
            dateTo?.takeIf { it.isNotBlank() }?.let { add("dateTo=${enc(it.trim())}") }
        }
        val qs = if (parts.isEmpty()) "" else "?" + parts.joinToString("&")
        val r = client.get(AppConfig.url("api/reports/operations") + qs)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun getStockValueReport(warehouseId: Long?): StockValueReportResponseDto {
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
