package com.example.warehouse_accounting_app.domain.model


object RolePermissions {

    fun canOpenUsers(role: UserRole): Boolean = role == UserRole.ADMIN

    fun canOpenCategories(role: UserRole): Boolean = true

    fun canEditCategories(role: UserRole): Boolean = role == UserRole.ADMIN

    fun canOpenProducts(role: UserRole): Boolean = true

    fun canEditProducts(role: UserRole): Boolean = role == UserRole.ADMIN

    fun canOpenStockBalances(role: UserRole): Boolean = true

    fun canOpenLowStock(role: UserRole): Boolean =
        role == UserRole.ADMIN || role == UserRole.STOREKEEPER || role == UserRole.MANAGER

    fun canCreateStockOperations(role: UserRole): Boolean =
        role == UserRole.ADMIN || role == UserRole.STOREKEEPER

    fun canOpenOperationHistory(role: UserRole): Boolean = true

    fun canOpenReports(role: UserRole): Boolean =
        role == UserRole.ADMIN || role == UserRole.MANAGER

    fun canOpenProfile(role: UserRole): Boolean = true
}
