package com.example.warehouse_accounting_app.presentation.users

import com.example.warehouse_accounting_app.domain.model.User

enum class UserListFilter {
    ALL,
    PENDING,
}

data class UserListState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val filter: UserListFilter = UserListFilter.ALL,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentUserId: Long? = null,
    val sessionExpired: Boolean = false,
    val roleDialogUserId: Long? = null,
    val actionInProgress: Boolean = false,
)
