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

class StockApi(private val client: HttpClient, private val json: Json) {

    suspend fun getStockBalances(warehouseId: Long?): List<StockBalanceResponseDto> {
        val url = AppConfig.url("api/stock/balances") + (warehouseId?.let { "?warehouseId=$it" } ?: "")
        val r = client.get(url)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun getLowStock(warehouseId: Long?): List<StockBalanceResponseDto> {
        val url = AppConfig.url("api/stock/low") + (warehouseId?.let { "?warehouseId=$it" } ?: "")
        val r = client.get(url)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun getProductHistory(productId: Long, operationType: String?, from: String?, to: String?, userId: Long?): List<StockOperationResponseDto> {
        val params = buildList {
            if (operationType != null) add("operationType=$operationType")
            if (from != null) add("from=$from")
            if (to != null) add("to=$to")
            if (userId != null) add("userId=$userId")
        }.joinToString("&")
        val url = AppConfig.url("api/stock/products/$productId/history") + if (params.isNotEmpty()) "?$params" else ""
        val r = client.get(url)
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
        return r.body()
    }

    suspend fun createReceipt(request: CreateReceiptRequestDto) {
        val r = client.post(AppConfig.url("api/stock/receipt")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
    }

    suspend fun createIssue(request: CreateIssueRequestDto) {
        val r = client.post(AppConfig.url("api/stock/issue")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
    }

    suspend fun createWriteOff(request: CreateWriteOffRequestDto) {
        val r = client.post(AppConfig.url("api/stock/write-off")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
    }

    suspend fun createInventory(request: CreateInventoryRequestDto) {
        val r = client.post(AppConfig.url("api/stock/inventory")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!r.status.isSuccess()) throw parseError(r.status.value, r.bodyAsText())
    }

    private fun parseError(code: Int, raw: String): ApiException {
        val message = runCatching { json.decodeFromString(ErrorResponseDto.serializer(), raw).message }.getOrDefault(raw)
        return ApiException(statusCode = code, message = message)
    }
}
