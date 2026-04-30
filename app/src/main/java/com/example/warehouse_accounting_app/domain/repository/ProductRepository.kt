package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(categoryId: Long?, search: String?): Result<List<Product>>
    suspend fun getProductById(id: Long): Result<Product>
    suspend fun createProduct(
        article: String,
        name: String,
        categoryId: Long,
        unit: String,
        purchasePrice: Double,
        salePrice: Double,
        minStock: Double,
    ): Result<Product>

    suspend fun updateProduct(
        id: Long,
        article: String,
        name: String,
        categoryId: Long,
        unit: String,
        purchasePrice: Double,
        salePrice: Double,
        minStock: Double,
        isActive: Boolean,
    ): Result<Product>

    suspend fun deleteProduct(id: Long): Result<Unit>
}
