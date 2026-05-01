package com.example.warehouse_accounting_app.data.remote.api

import com.example.warehouse_accounting_app.core.config.AppConfig
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateCategoryRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.UpdateCategoryRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.response.CategoryResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.ErrorResponseDto
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

class CategoryApi(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun getCategories(activeOnly: Boolean = true): List<CategoryResponseDto> {
        val url = AppConfig.url("api/categories") + if (!activeOnly) "?activeOnly=false" else ""
        val response = client.get(url)
        if (!response.status.isSuccess()) throw parseError(response.status.value, response.bodyAsText())
        return response.body()
    }

    suspend fun getCategoryById(id: Long): CategoryResponseDto {
        val response = client.get(AppConfig.url("api/categories/$id"))
        if (!response.status.isSuccess()) throw parseError(response.status.value, response.bodyAsText())
        return response.body()
    }

    suspend fun createCategory(request: CreateCategoryRequestDto): CategoryResponseDto {
        val response = client.post(AppConfig.url("api/categories")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!response.status.isSuccess()) throw parseError(response.status.value, response.bodyAsText())
        return response.body()
    }

    suspend fun updateCategory(id: Long, request: UpdateCategoryRequestDto): CategoryResponseDto {
        val response = client.put(AppConfig.url("api/categories/$id")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(request)
        }
        if (!response.status.isSuccess()) throw parseError(response.status.value, response.bodyAsText())
        return response.body()
    }

    suspend fun deleteCategory(id: Long): CategoryResponseDto {
        val response = client.delete(AppConfig.url("api/categories/$id"))
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
