package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val message: String,
    val details: String? = null,
)
