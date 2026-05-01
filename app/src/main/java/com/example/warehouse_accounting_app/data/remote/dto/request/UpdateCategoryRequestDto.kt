package com.example.warehouse_accounting_app.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCategoryRequestDto(
    val name: String,
    val description: String? = null,
    val isActive: Boolean,
)
