package com.example.warehouse_accounting_app.domain.model

data class Product(
    val id: Long,
    val article: String,
    val name: String,
    val categoryId: Long,
    val categoryName: String?,
    val unit: String,
    val purchasePrice: Double,
    val salePrice: Double,
    val minStock: Double,
    val isActive: Boolean,
)
