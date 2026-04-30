package com.example.warehouse_accounting_app.data.remote.api

import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.data.remote.dto.request.LoginRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.RegisterRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.response.AuthResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.CurrentUserResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.ErrorResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class AuthApi(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun login(body: LoginRequestDto): AuthResponseDto = requestAuth(client.post("api/auth/login") { setBody(body) })

    suspend fun register(body: RegisterRequestDto): AuthResponseDto = requestAuth(client.post("api/auth/register") { setBody(body) })

    suspend fun me(): CurrentUserResponseDto {
        val response = client.get("api/auth/me")
        return requestMe(response)
    }

    private suspend fun requestAuth(response: HttpResponse): AuthResponseDto {
        if (!response.status.isSuccess()) {
            throw parseError(response.status.value, response.bodyAsText())
        }
        return response.body()
    }

    private suspend fun requestMe(response: HttpResponse): CurrentUserResponseDto {
        if (!response.status.isSuccess()) {
            throw parseError(response.status.value, response.bodyAsText())
        }
        return response.body()
    }

    private fun parseError(code: Int, raw: String): ApiException {
        val message = runCatching { json.decodeFromString(ErrorResponseDto.serializer(), raw).message }.getOrDefault(raw)
        return ApiException(code, message)
    }
}
