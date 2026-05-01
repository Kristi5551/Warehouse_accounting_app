package com.example.warehouse_accounting_app.data.remote.api

import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.data.remote.dto.request.ApproveUserRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.ChangeUserRoleRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.response.ErrorResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.UserResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class UserApi(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun getUsers(): List<UserResponseDto> {
        val response = client.get("api/users")
        return parseList(response)
    }

    suspend fun getPendingUsers(): List<UserResponseDto> {
        val response = client.get("api/users/pending")
        return parseList(response)
    }

    suspend fun approveUser(id: Long): UserResponseDto {
        val response = client.patch("api/users/$id/approve") {
            setBody(ApproveUserRequestDto())
        }
        return parseUser(response)
    }

    suspend fun blockUser(id: Long): UserResponseDto {
        val response = client.patch("api/users/$id/block") {
            setBody(ApproveUserRequestDto())
        }
        return parseUser(response)
    }

    suspend fun unblockUser(id: Long): UserResponseDto {
        val response = client.patch("api/users/$id/unblock") {
            setBody(ApproveUserRequestDto())
        }
        return parseUser(response)
    }

    suspend fun changeUserRole(id: Long, role: String): UserResponseDto {
        val response = client.patch("api/users/$id/role") {
            setBody(ChangeUserRoleRequestDto(role = role))
        }
        return parseUser(response)
    }

    private suspend fun parseList(response: io.ktor.client.statement.HttpResponse): List<UserResponseDto> {
        if (!response.status.isSuccess()) {
            throw parseError(response.status.value, response.bodyAsText())
        }
        return response.body()
    }

    private suspend fun parseUser(response: io.ktor.client.statement.HttpResponse): UserResponseDto {
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
