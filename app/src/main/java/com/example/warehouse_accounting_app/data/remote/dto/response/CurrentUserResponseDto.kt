package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CurrentUserResponseDto(
    val id: Long,
    val email: String,
    val fullName: String,
    val role: String,
    val status: String,
)
