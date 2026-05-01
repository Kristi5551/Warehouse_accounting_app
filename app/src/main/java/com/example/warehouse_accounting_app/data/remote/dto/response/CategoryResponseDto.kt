package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponseDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
)
