package com.example.warehouse_accounting_app.presentation.users

import com.example.warehouse_accounting_app.domain.model.UserRole

sealed interface UserListEvent {
    data class FilterChanged(val filter: UserListFilter) : UserListEvent
    data object Refresh : UserListEvent
    data class Approve(val id: Long) : UserListEvent
    data class Block(val id: Long) : UserListEvent
    data class Unblock(val id: Long) : UserListEvent
    data class OpenRoleDialog(val id: Long) : UserListEvent
    data object CloseRoleDialog : UserListEvent
    data class ConfirmRole(val userId: Long, val role: UserRole) : UserListEvent
    data object ClearMessages : UserListEvent
    data object SessionExpiredConsumed : UserListEvent
}
