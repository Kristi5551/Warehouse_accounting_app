package com.example.warehouse_accounting_app.core.ui.format

import com.example.warehouse_accounting_app.domain.model.UserRole

fun UserRole.ruLabel(): String = when (this) {
    UserRole.ADMIN -> "Администратор"
    UserRole.STOREKEEPER -> "Кладовщик"
    UserRole.MANAGER -> "Менеджер"
}
