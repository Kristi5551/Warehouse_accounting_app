package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun createCategory(name: String, description: String?): Result<Category>
    suspend fun updateCategory(id: Long, name: String, description: String?, isActive: Boolean): Result<Category>
    suspend fun deleteCategory(id: Long): Result<Unit>
}
