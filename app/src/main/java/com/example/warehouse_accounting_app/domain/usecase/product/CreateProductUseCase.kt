package com.example.warehouse_accounting_app.domain.usecase.product

import com.example.warehouse_accounting_app.domain.repository.ProductRepository

class CreateProductUseCase(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(
        article: String,
        name: String,
        categoryId: Long,
        unit: String,
        purchasePrice: Double,
        salePrice: Double,
        minStock: Double,
    ) = repository.createProduct(article, name, categoryId, unit, purchasePrice, salePrice, minStock)
}
