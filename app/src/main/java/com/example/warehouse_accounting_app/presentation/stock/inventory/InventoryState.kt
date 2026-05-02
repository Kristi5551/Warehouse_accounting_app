package com.example.warehouse_accounting_app.presentation.stock.inventory

import com.example.warehouse_accounting_app.domain.model.Product

data class InventoryState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isLoadingBalance: Boolean = false,
    val products: List<Product> = emptyList(),
    val selectedProduct: Product? = null,
    /** Учётный остаток на складе (из stock balances). */
    val bookedQuantity: Double? = null,
    val actualQuantity: String = "",
    val comment: String = "",
    val productError: String? = null,
    val actualError: String? = null,
    val errorMessage: String? = null,
)
