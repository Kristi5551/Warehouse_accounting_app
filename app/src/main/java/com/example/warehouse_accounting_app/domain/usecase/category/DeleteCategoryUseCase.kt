package com.example.warehouse_accounting_app.domain.usecase.category

import com.example.warehouse_accounting_app.domain.repository.CategoryRepository

class DeleteCategoryUseCase(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteCategory(id)
}
