package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.connectivityMessage
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.remote.api.CategoryApi
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateCategoryRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.UpdateCategoryRequestDto
import com.example.warehouse_accounting_app.domain.model.Category
import com.example.warehouse_accounting_app.domain.repository.CategoryRepository
import java.io.IOException

class CategoryRepositoryImpl(
    private val api: CategoryApi,
) : CategoryRepository {

    override suspend fun getCategories(activeOnly: Boolean): AppResult<List<Category>> =
        try {
            AppResult.Success(api.getCategories(activeOnly).map { it.toDomain() })
        } catch (e: ApiException) {
            logApiException(e, "GET /api/categories")
            AppResult.Error(e.message ?: "Не удалось загрузить категории", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/categories")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/categories")
            AppResult.Error("Не удалось загрузить категории", e)
        }

    override suspend fun getCategoryById(id: Long): AppResult<Category> =
        try {
            AppResult.Success(api.getCategoryById(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "GET /api/categories/$id")
            AppResult.Error(e.message ?: "Категория не найдена", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/categories/$id")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/categories/$id")
            AppResult.Error("Не удалось загрузить категорию", e)
        }

    override suspend fun createCategory(name: String, description: String?): AppResult<Category> =
        try {
            AppResult.Success(api.createCategory(CreateCategoryRequestDto(name, description)).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "POST /api/categories")
            AppResult.Error(e.message ?: "Не удалось создать категорию", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "POST /api/categories")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "POST /api/categories")
            AppResult.Error("Не удалось создать категорию", e)
        }

    override suspend fun updateCategory(id: Long, name: String, description: String?, isActive: Boolean): AppResult<Category> =
        try {
            AppResult.Success(api.updateCategory(id, UpdateCategoryRequestDto(name, description, isActive)).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "PUT /api/categories/$id")
            AppResult.Error(e.message ?: "Не удалось обновить категорию", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PUT /api/categories/$id")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "PUT /api/categories/$id")
            AppResult.Error("Не удалось обновить категорию", e)
        }

    override suspend fun deleteCategory(id: Long): AppResult<Category> =
        try {
            AppResult.Success(api.deleteCategory(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "DELETE /api/categories/$id")
            AppResult.Error(e.message ?: "Не удалось деактивировать категорию", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "DELETE /api/categories/$id")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "DELETE /api/categories/$id")
            AppResult.Error("Не удалось деактивировать категорию", e)
        }
}
