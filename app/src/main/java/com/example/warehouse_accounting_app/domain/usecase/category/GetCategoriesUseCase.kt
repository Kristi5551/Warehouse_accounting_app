package com.example.warehouse_accounting_app.domain.usecase.category

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Category
import com.example.warehouse_accounting_app.domain.repository.CategoryRepository

class GetCategoriesUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(activeOnly: Boolean = true): AppResult<List<Category>> =
        repository.getCategories(activeOnly)
}
