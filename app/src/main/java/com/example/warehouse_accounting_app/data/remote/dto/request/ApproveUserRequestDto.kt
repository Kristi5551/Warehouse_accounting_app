package com.example.warehouse_accounting_app.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ApproveUserRequestDto(
    val note: String? = null,
)
