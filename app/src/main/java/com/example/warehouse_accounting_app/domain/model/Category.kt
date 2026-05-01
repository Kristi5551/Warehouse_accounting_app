package com.example.warehouse_accounting_app.domain.model

data class Category(
    val id: Long,
    val name: String,
    val description: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
)
