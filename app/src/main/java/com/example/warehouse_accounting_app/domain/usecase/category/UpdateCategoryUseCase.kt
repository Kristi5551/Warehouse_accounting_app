package com.example.warehouse_accounting_app.domain.usecase.category

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Category
import com.example.warehouse_accounting_app.domain.repository.CategoryRepository

class UpdateCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(id: Long, name: String, description: String?, isActive: Boolean): AppResult<Category> =
        repository.updateCategory(id, name, description, isActive)
}
