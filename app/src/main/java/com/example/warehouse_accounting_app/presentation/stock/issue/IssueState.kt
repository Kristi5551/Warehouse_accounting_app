package com.example.warehouse_accounting_app.presentation.stock.issue

import com.example.warehouse_accounting_app.domain.model.Product

data class IssueState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val products: List<Product> = emptyList(),
    val selectedProduct: Product? = null,
    val quantity: String = "",
    val reason: String = "",
    val comment: String = "",
    val productError: String? = null,
    val quantityError: String? = null,
    val errorMessage: String? = null,
)
