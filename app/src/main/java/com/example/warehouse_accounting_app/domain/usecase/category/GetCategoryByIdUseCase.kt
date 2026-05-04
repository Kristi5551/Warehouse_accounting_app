package com.example.warehouse_accounting_app.domain.usecase.category

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Category
import com.example.warehouse_accounting_app.domain.repository.CategoryRepository

class GetCategoryByIdUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(id: Long): AppResult<Category> = repository.getCategoryById(id)
}
