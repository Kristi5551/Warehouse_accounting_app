package com.example.warehouse_accounting_app.domain.model

data class Warehouse(
    val id: Long,
    val name: String,
    val address: String?,
    val isActive: Boolean,
)
