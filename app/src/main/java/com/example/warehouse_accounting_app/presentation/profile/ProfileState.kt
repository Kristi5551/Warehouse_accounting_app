package com.example.warehouse_accounting_app.presentation.profile

import com.example.warehouse_accounting_app.domain.model.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
