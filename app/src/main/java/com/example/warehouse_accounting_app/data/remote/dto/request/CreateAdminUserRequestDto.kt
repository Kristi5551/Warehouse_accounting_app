package com.example.warehouse_accounting_app.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateAdminUserRequestDto(
    val fullName: String,
    val email: String,
    val password: String,
)
