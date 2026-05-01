package com.example.warehouse_accounting_app.data.mapper

import com.example.warehouse_accounting_app.data.remote.dto.response.CategoryResponseDto
import com.example.warehouse_accounting_app.domain.model.Category

fun CategoryResponseDto.toDomain(): Category = Category(
    id = id,
    name = name,
    description = description,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
