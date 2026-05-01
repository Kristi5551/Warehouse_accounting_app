package com.example.warehouse_accounting_app.presentation.categories

sealed interface CategoryListEvent {
    data object NavigateToCreate : CategoryListEvent
    data class NavigateToEdit(val categoryId: Long) : CategoryListEvent
    data class ShowSuccess(val message: String) : CategoryListEvent
    data class ShowError(val message: String) : CategoryListEvent
    data object SessionExpired : CategoryListEvent
}

sealed interface CategoryEditEvent {
    data object SaveSuccess : CategoryEditEvent
    data class ShowError(val message: String) : CategoryEditEvent
    data object SessionExpired : CategoryEditEvent
}
