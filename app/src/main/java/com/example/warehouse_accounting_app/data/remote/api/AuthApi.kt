package com.example.warehouse_accounting_app.data.remote.api

import com.example.warehouse_accounting_app.core.config.AppConfig
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.data.remote.dto.request.LoginRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.RegisterRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.response.AuthResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.ErrorResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.UserResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class AuthApi(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun register(body: RegisterRequestDto): UserResponseDto {
        val response = client.post(AppConfig.url("api/auth/register")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(body)
        }
        return parseUserResponse(response)
    }

    suspend fun login(body: LoginRequestDto): AuthResponseDto {
        val response = client.post(AppConfig.url("api/auth/login")) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(body)
        }
        if (!response.status.isSuccess()) {
            throw parseError(response.status.value, response.bodyAsText())
        }
        return response.body()
    }

    suspend fun me(): UserResponseDto {
        val response = client.get(AppConfig.url("api/auth/me"))
        if (!response.status.isSuccess()) {
            throw parseError(response.status.value, response.bodyAsText())
        }
        return response.body()
    }

    private suspend fun parseUserResponse(response: HttpResponse): UserResponseDto {
        if (!response.status.isSuccess()) {
            throw parseError(response.status.value, response.bodyAsText())
        }
        return response.body()
    }

    private fun parseError(code: Int, raw: String): ApiException {
        val message = runCatching { json.decodeFromString(ErrorResponseDto.serializer(), raw).message }.getOrDefault(raw)
        return ApiException(statusCode = code, message = message)
    }
}
