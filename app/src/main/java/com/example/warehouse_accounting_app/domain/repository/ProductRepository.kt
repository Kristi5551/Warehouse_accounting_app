package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(search: String? = null, categoryId: Long? = null, activeOnly: Boolean = true): AppResult<List<Product>>
    suspend fun getProductById(id: Long): AppResult<Product>
    suspend fun createProduct(
        article: String, name: String, categoryId: Long, unit: String,
        purchasePrice: Double, salePrice: Double, minStock: Double,
    ): AppResult<Product>
    suspend fun updateProduct(
        id: Long, article: String, name: String, categoryId: Long, unit: String,
        purchasePrice: Double, salePrice: Double, minStock: Double, isActive: Boolean,
    ): AppResult<Product>
    suspend fun deleteProduct(id: Long): AppResult<Product>
}
