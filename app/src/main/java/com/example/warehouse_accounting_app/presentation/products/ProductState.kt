package com.example.warehouse_accounting_app.presentation.products

import com.example.warehouse_accounting_app.domain.model.Category
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.StockOperation

data class ProductListState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null,
    val activeOnly: Boolean = true,
    val errorMessage: String? = null,
    val isRoleLoading: Boolean = false,
    val roleErrorMessage: String? = null,
    val isAdminUser: Boolean = false,
    val isCategoriesLoading: Boolean = false,
    val categoriesErrorMessage: String? = null,
)

val ProductListState.filtered: List<Product>
    get() {
        var list = products
        if (searchQuery.isNotBlank())
            list = list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.article.contains(searchQuery, ignoreCase = true)
            }
        if (selectedCategoryId != null)
            list = list.filter { it.categoryId == selectedCategoryId }
        return list
    }

data class ProductEditState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val categories: List<Category> = emptyList(),
    val article: String = "",
    val name: String = "",
    val selectedCategoryId: Long? = null,
    val unit: String = "",
    val purchasePrice: String = "",
    val salePrice: String = "",
    val minStock: String = "",
    val isActive: Boolean = true,
    val articleError: String? = null,
    val nameError: String? = null,
    val categoryError: String? = null,
    val unitError: String? = null,
    val purchasePriceError: String? = null,
    val salePriceError: String? = null,
    val minStockError: String? = null,
    val errorMessage: String? = null,
    val editingProduct: Product? = null,
)

val ProductEditState.isEditMode: Boolean get() = editingProduct != null

// ── Details ───────────────────────────────────────────────────────────────────

data class ProductDetailsState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val errorMessage: String? = null,
    val isAdmin: Boolean = false,
    val history: List<StockOperation> = emptyList(),
    val historyLoading: Boolean = false,
)
