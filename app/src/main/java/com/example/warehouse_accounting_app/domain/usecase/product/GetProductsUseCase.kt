package com.example.warehouse_accounting_app.domain.usecase.product

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.repository.ProductRepository

class GetProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(search: String? = null, categoryId: Long? = null, activeOnly: Boolean = true): AppResult<List<Product>> =
        repository.getProducts(search, categoryId, activeOnly)
}
