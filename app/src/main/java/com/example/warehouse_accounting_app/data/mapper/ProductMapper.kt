package com.example.warehouse_accounting_app.data.mapper

import com.example.warehouse_accounting_app.data.remote.dto.response.ProductResponseDto
import com.example.warehouse_accounting_app.domain.model.Product

fun ProductResponseDto.toDomain(): Product = Product(
    id = id,
    article = article,
    name = name,
    categoryId = categoryId,
    categoryName = categoryName,
    unit = unit,
    purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
    salePrice = salePrice.toDoubleOrNull() ?: 0.0,
    minStock = minStock.toDoubleOrNull() ?: 0.0,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
