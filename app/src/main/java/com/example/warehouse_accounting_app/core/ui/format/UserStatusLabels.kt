package com.example.warehouse_accounting_app.core.ui.format

import com.example.warehouse_accounting_app.domain.model.UserStatus

fun UserStatus.ruLabel(): String = when (this) {
    UserStatus.ACTIVE -> "Активен"
    UserStatus.PENDING -> "Ожидает подтверждения"
    UserStatus.BLOCKED -> "Заблокирован"
}
