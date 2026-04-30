package com.example.warehouse_accounting_app.domain.usecase.category

import com.example.warehouse_accounting_app.domain.repository.CategoryRepository

class UpdateCategoryUseCase(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(id: Long, name: String, description: String?, isActive: Boolean) =
        repository.updateCategory(id, name, description, isActive)
}
