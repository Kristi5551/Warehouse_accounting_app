package com.example.warehouse_accounting_app.domain.usecase.product

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.repository.ProductRepository

class UpdateProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(
        id: Long, article: String, name: String, categoryId: Long, unit: String,
        purchasePrice: Double, salePrice: Double, minStock: Double, isActive: Boolean,
    ): AppResult<Product> = repository.updateProduct(id, article, name, categoryId, unit, purchasePrice, salePrice, minStock, isActive)
}
