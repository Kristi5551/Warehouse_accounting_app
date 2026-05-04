package com.example.warehouse_accounting_app.domain.usecase.product

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.repository.ProductRepository

class DeleteProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Long): AppResult<Product> = repository.deleteProduct(id)
}
