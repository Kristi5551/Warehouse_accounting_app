package com.example.warehouse_accounting_app.presentation.products

sealed interface ProductListEvent {
    data object NavigateToCreate : ProductListEvent
    data class NavigateToEdit(val productId: Long) : ProductListEvent
    data class NavigateToDetails(val productId: Long) : ProductListEvent
    data class ShowSuccess(val message: String) : ProductListEvent
    data class ShowError(val message: String) : ProductListEvent
    data object SessionExpired : ProductListEvent
}

sealed interface ProductEditEvent {
    data object SaveSuccess : ProductEditEvent
    data class ShowError(val message: String) : ProductEditEvent
    data object SessionExpired : ProductEditEvent
}
