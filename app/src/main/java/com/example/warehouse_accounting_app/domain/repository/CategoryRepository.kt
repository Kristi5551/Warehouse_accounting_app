package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(activeOnly: Boolean = true): AppResult<List<Category>>
    suspend fun getCategoryById(id: Long): AppResult<Category>
    suspend fun createCategory(name: String, description: String?): AppResult<Category>
    suspend fun updateCategory(id: Long, name: String, description: String?, isActive: Boolean): AppResult<Category>
    suspend fun deleteCategory(id: Long): AppResult<Category>
}
