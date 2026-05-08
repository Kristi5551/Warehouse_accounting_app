package com.example.warehouse_accounting_app.data.remote.api

import com.example.warehouse_accounting_app.core.config.AppConfig
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateInventoryRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateIssueRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateReceiptRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateWriteOffRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.response.ErrorResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockBalanceResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.StockOperationResponseDto
import com.example.warehouse_accounting_app.domain.model.StockStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class StockApi(private val client: HttpClient, private val json: Json) {

    private fun enc(s: String) = URLEncoder.encode(s, StandardCharsets.UTF_8)

    suspend fun getStockBalances(search: String?, categoryId: Long?, status: StockStatus?): List<StockBalanceResponseDto> {
        val parts = buildList {
            search?.takeIf { it.isNotBlank() }?.let { add("search=${enc(it.trim())}") }
            categoryId?.let { add("categoryId=$it") }
            status?.let { add("status=${it.name}") }
        }
        val qs = if (parts.isEmpty()) "" else "?" + parts.joinToString("&")
        val r = client.get(AppConfig.url("api/stock/balances") + qs)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun getLowStock(): List<StockBalanceResponseDto> {
        val r = client.get(AppConfig.url("api/stock/low"))
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun getOperations(
        type: String?,
        productId: Long?,
        userId: Long?,
        dateFrom: String?,
        dateTo: String?,
    ): List<StockOperationResponseDto> {
        val parts = buildList {
            type?.let { add("type=${enc(it)}") }
            productId?.let { add("productId=$it") }
            userId?.let { add("userId=$it") }
            dateFrom?.takeIf { it.isNotBlank() }?.let { add("dateFrom=${enc(it.trim())}") }
            dateTo?.takeIf { it.isNotBlank() }?.let { add("dateTo=${enc(it.trim())}") }
        }
        val qs = if (parts.isEmpty()) "" else "?" + parts.joinToString("&")
        val r = client.get(AppConfig.url("api/operations") + qs)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun getProductHistory(
        productId: Long,
        operationType: String?,
        userId: Long?,
        dateFrom: String?,
        dateTo: String?,
    ): List<StockOperationResponseDto> {
        val params = buildList {
            if (operationType != null) add("type=${enc(operationType)}")
            if (userId != null) add("userId=$userId")
            dateFrom?.takeIf { it.isNotBlank() }?.let { add("dateFrom=${enc(it.trim())}") }
            dateTo?.takeIf { it.isNotBlank() }?.let { add("dateTo=${enc(it.trim())}") }
        }.joinToString("&")
        val url = AppConfig.url("api/stock/products/$productId/history") + if (params.isNotEmpty()) "?$params" else ""
        val r = client.get(url)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun createReceipt(request: CreateReceiptRequestDto): StockOperationResponseDto {
        val r = client.post(AppConfig.url("api/stock/receipt")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun createIssue(request: CreateIssueRequestDto): StockOperationResponseDto {
        val r = client.post(AppConfig.url("api/stock/issue")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun createWriteOff(request: CreateWriteOffRequestDto): StockOperationResponseDto {
        val r = client.post(AppConfig.url("api/stock/write-off")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun createInventory(request: CreateInventoryRequestDto): StockOperationResponseDto {
        val r = client.post(AppConfig.url("api/stock/inventory")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    private fun parseError(code: Int, raw: String): ApiException {
        val message = runCatching { json.decodeFromString(ErrorResponseDto.serializer(), raw).message }.getOrDefault(raw)
        return ApiException(statusCode = code, message = message)
    }
}
