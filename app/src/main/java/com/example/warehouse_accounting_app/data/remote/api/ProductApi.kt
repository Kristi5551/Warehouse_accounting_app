package com.example.warehouse_accounting_app.data.remote.api

import com.example.warehouse_accounting_app.core.config.AppConfig
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateProductRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.UpdateProductRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.response.ErrorResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.ProductResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class ProductApi(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun getProducts(search: String?, categoryId: Long?, activeOnly: Boolean): List<ProductResponseDto> {
        val params = buildList {
            if (!search.isNullOrBlank()) add("search=${search.trim()}")
            if (categoryId != null) add("categoryId=$categoryId")
            if (!activeOnly) add("activeOnly=false")
        }.joinToString("&")
        val url = AppConfig.url("api/products") + if (params.isNotEmpty()) "?$params" else ""
        val response = client.get(url)
        if (!response.status.isSuccess()) throw parseError(response.status.value, response.bodyAsText())
        return response.body()
    }

    suspend fun getProductById(id: Long): ProductResponseDto {
        val response = client.get(AppConfig.url("api/products/$id"))
        if (!response.status.isSuccess()) throw parseError(response.status.value, response.bodyAsText())
        return response.body()
    }

    suspend fun createProduct(request: CreateProductRequestDto): ProductResponseDto {
        val response = client.post(AppConfig.url("api/products")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!response.status.isSuccess()) throw parseError(response.status.value, response.bodyAsText())
        return response.body()
    }

    suspend fun updateProduct(id: Long, request: UpdateProductRequestDto): ProductResponseDto {
        val response = client.put(AppConfig.url("api/products/$id")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!response.status.isSuccess()) throw parseError(response.status.value, response.bodyAsText())
        return response.body()
    }

    suspend fun deleteProduct(id: Long): ProductResponseDto {
        val response = client.delete(AppConfig.url("api/products/$id"))
        if (!response.status.isSuccess()) throw parseError(response.status.value, response.bodyAsText())
        return response.body()
    }

    private fun parseError(code: Int, raw: String): ApiException {
        val message = runCatching {
            json.decodeFromString(ErrorResponseDto.serializer(), raw).message
        }.getOrDefault(raw)
        return ApiException(statusCode = code, message = message)
    }
}
