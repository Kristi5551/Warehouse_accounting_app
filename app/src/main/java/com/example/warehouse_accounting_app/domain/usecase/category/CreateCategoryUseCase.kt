package com.example.warehouse_accounting_app.domain.usecase.category

import com.example.warehouse_accounting_app.domain.repository.CategoryRepository

class CreateCategoryUseCase(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(name: String, description: String?) = repository.createCategory(name, description)
}
