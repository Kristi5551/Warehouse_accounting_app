package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserBriefResponseDto(
    val id: Long,
    val fullName: String,
)
