package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.data.mapper.apiFailure
import com.example.warehouse_accounting_app.data.mapper.networkFailure
import com.example.warehouse_accounting_app.data.mapper.unknownFailure
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.remote.api.CategoryApi
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateCategoryRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.UpdateCategoryRequestDto
import com.example.warehouse_accounting_app.domain.model.Category
import com.example.warehouse_accounting_app.domain.repository.CategoryRepository
import com.example.warehouse_accounting_app.domain.result.AppResult
import java.io.IOException

class CategoryRepositoryImpl(
    private val api: CategoryApi,
) : CategoryRepository {

    override suspend fun getCategories(activeOnly: Boolean): AppResult<List<Category>> =
        try {
            AppResult.Success(api.getCategories(activeOnly).map { it.toDomain() })
        } catch (e: ApiException) {
            logApiException(e, "GET /api/categories")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/categories")
            networkFailure(e, "Не удалось загрузить категории")
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/categories")
            unknownFailure(e, "Не удалось загрузить категории")
        }

    override suspend fun getCategoryById(id: Long): AppResult<Category> =
        try {
            AppResult.Success(api.getCategoryById(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "GET /api/categories/$id")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/categories/$id")
            networkFailure(e, "Не удалось загрузить категорию")
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/categories/$id")
            unknownFailure(e, "Не удалось загрузить категорию")
        }

    override suspend fun createCategory(name: String, description: String?): AppResult<Category> =
        try {
            AppResult.Success(api.createCategory(CreateCategoryRequestDto(name, description)).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "POST /api/categories")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "POST /api/categories")
            networkFailure(e, "Не удалось создать категорию")
        } catch (e: Exception) {
            logNetworkFailure(e, "POST /api/categories")
            unknownFailure(e, "Не удалось создать категорию")
        }

    override suspend fun updateCategory(id: Long, name: String, description: String?, isActive: Boolean): AppResult<Category> =
        try {
            AppResult.Success(
                api.updateCategory(id, UpdateCategoryRequestDto(name, description, isActive)).toDomain(),
            )
        } catch (e: ApiException) {
            logApiException(e, "PUT /api/categories/$id")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PUT /api/categories/$id")
            networkFailure(e, "Не удалось обновить категорию")
        } catch (e: Exception) {
            logNetworkFailure(e, "PUT /api/categories/$id")
            unknownFailure(e, "Не удалось обновить категорию")
        }

    override suspend fun deleteCategory(id: Long): AppResult<Category> =
        try {
            AppResult.Success(api.deleteCategory(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "DELETE /api/categories/$id")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "DELETE /api/categories/$id")
            networkFailure(e, "Не удалось деактивировать категорию")
        } catch (e: Exception) {
            logNetworkFailure(e, "DELETE /api/categories/$id")
            unknownFailure(e, "Не удалось деактивировать категорию")
        }
}
