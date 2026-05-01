package com.example.warehouse_accounting_app.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequestDto(
    val article: String,
    val name: String,
    val categoryId: Long,
    val unit: String,
    val purchasePrice: String,
    val salePrice: String,
    val minStock: String,
)
