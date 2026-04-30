package com.example.warehouse_accounting_app.domain.usecase.product

import com.example.warehouse_accounting_app.domain.repository.ProductRepository

class GetProductsUseCase(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(categoryId: Long?, search: String?) = repository.getProducts(categoryId, search)
}
