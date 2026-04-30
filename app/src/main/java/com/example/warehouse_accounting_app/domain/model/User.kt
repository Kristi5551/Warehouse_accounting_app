package com.example.warehouse_accounting_app.domain.model

data class User(
    val id: Long,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val status: UserStatus,
)
