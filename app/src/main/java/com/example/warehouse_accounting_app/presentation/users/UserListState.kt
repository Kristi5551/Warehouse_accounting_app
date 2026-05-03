package com.example.warehouse_accounting_app.presentation.users

import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserStatus

enum class UserListFilter(val label: String) {
    ALL("Все"),
    PENDING("Ожидают"),
    ACTIVE("Активные"),
    BLOCKED("Заблокированные"),
}

fun UserListFilter.matchesStatus(status: UserStatus): Boolean = when (this) {
    UserListFilter.ALL -> true
    UserListFilter.PENDING -> status == UserStatus.PENDING
    UserListFilter.ACTIVE -> status == UserStatus.ACTIVE
    UserListFilter.BLOCKED -> status == UserStatus.BLOCKED
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
    val createAdminDialogVisible: Boolean = false,
    val newAdminFullName: String = "",
    val newAdminEmail: String = "",
    val newAdminPassword: String = "",
    val newAdminRepeatPassword: String = "",
    val createAdminFormError: String? = null,
)
