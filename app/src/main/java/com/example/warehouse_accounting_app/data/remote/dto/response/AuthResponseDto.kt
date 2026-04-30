package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val token: String,
    val user: UserResponseDto,
)
