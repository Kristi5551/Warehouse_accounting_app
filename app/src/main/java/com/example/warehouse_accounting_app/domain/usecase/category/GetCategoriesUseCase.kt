package com.example.warehouse_accounting_app.domain.usecase.category

import com.example.warehouse_accounting_app.domain.repository.CategoryRepository

class GetCategoriesUseCase(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke() = repository.getCategories()
}
