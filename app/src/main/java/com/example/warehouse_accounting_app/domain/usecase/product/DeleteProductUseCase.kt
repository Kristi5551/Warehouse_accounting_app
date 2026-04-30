package com.example.warehouse_accounting_app.domain.usecase.product

import com.example.warehouse_accounting_app.domain.repository.ProductRepository

class DeleteProductUseCase(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(id: Long) = repository.deleteProduct(id)
}
