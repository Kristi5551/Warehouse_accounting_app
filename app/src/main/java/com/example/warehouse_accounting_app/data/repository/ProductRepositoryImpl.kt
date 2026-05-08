package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.data.mapper.apiFailure
import com.example.warehouse_accounting_app.data.mapper.networkFailure
import com.example.warehouse_accounting_app.data.mapper.unknownFailure
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.remote.api.ProductApi
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateProductRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.UpdateProductRequestDto
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.repository.ProductRepository
import com.example.warehouse_accounting_app.domain.result.AppResult
import java.io.IOException
import java.util.Locale

class ProductRepositoryImpl(private val api: ProductApi) : ProductRepository {

    override suspend fun getProducts(search: String?, categoryId: Long?, activeOnly: Boolean): AppResult<List<Product>> =
        try {
            AppResult.Success(api.getProducts(search, categoryId, activeOnly).map { it.toDomain() })
        } catch (e: ApiException) {
            logApiException(e, "GET /api/products")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/products")
            networkFailure(e, "Ошибка загрузки товаров")
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/products")
            unknownFailure(e, "Ошибка загрузки товаров")
        }

    override suspend fun getProductById(id: Long): AppResult<Product> =
        try {
            AppResult.Success(api.getProductById(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "GET /api/products/$id")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/products/$id")
            networkFailure(e, "Ошибка загрузки товара")
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/products/$id")
            unknownFailure(e, "Ошибка загрузки товара")
        }

    override suspend fun createProduct(
        article: String, name: String, categoryId: Long, unit: String,
        purchasePrice: Double, salePrice: Double, minStock: Double,
    ): AppResult<Product> =
        try {
            AppResult.Success(
                api.createProduct(
                    CreateProductRequestDto(
                        article, name, categoryId, unit,
                        String.format(Locale.US, "%.2f", purchasePrice),
                        String.format(Locale.US, "%.2f", salePrice),
                        String.format(Locale.US, "%.3f", minStock),
                    ),
                ).toDomain(),
            )
        } catch (e: ApiException) {
            logApiException(e, "POST /api/products")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "POST /api/products")
            networkFailure(e, "Ошибка создания товара")
        } catch (e: Exception) {
            logNetworkFailure(e, "POST /api/products")
            unknownFailure(e, "Ошибка создания товара")
        }

    override suspend fun updateProduct(
        id: Long, article: String, name: String, categoryId: Long, unit: String,
        purchasePrice: Double, salePrice: Double, minStock: Double, isActive: Boolean,
    ): AppResult<Product> =
        try {
            AppResult.Success(
                api.updateProduct(
                    id,
                    UpdateProductRequestDto(
                        article, name, categoryId, unit,
                        String.format(Locale.US, "%.2f", purchasePrice),
                        String.format(Locale.US, "%.2f", salePrice),
                        String.format(Locale.US, "%.3f", minStock),
                        isActive,
                    ),
                ).toDomain(),
            )
        } catch (e: ApiException) {
            logApiException(e, "PUT /api/products/$id")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PUT /api/products/$id")
            networkFailure(e, "Ошибка обновления товара")
        } catch (e: Exception) {
            logNetworkFailure(e, "PUT /api/products/$id")
            unknownFailure(e, "Ошибка обновления товара")
        }

    override suspend fun deleteProduct(id: Long): AppResult<Product> =
        try {
            AppResult.Success(api.deleteProduct(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "DELETE /api/products/$id")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "DELETE /api/products/$id")
            networkFailure(e, "Ошибка деактивации товара")
        } catch (e: Exception) {
            logNetworkFailure(e, "DELETE /api/products/$id")
            unknownFailure(e, "Ошибка деактивации товара")
        }
}
