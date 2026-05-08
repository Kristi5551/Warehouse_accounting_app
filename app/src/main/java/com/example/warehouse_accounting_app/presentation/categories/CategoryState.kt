package com.example.warehouse_accounting_app.presentation.categories

import com.example.warehouse_accounting_app.domain.model.Category

data class CategoryListState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isAdminUser: Boolean = false,
)

val CategoryListState.filtered: List<Category>
    get() = if (searchQuery.isBlank()) categories
    else categories.filter { it.name.contains(searchQuery, ignoreCase = true) }

data class CategoryEditState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val name: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val nameError: String? = null,
    val errorMessage: String? = null,
    val editingCategory: Category? = null,
)

val CategoryEditState.isEditMode: Boolean get() = editingCategory != null
