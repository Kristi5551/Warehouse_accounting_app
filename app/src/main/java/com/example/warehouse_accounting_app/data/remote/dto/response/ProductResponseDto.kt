package com.example.warehouse_accounting_app.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponseDto(
    val id: Long,
    val article: String,
    val name: String,
    val categoryId: Long,
    val categoryName: String? = null,
    val unit: String,
    val purchasePrice: String,
    val salePrice: String,
    val minStock: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
)
