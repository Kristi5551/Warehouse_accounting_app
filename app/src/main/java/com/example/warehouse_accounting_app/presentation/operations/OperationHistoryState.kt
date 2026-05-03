package com.example.warehouse_accounting_app.presentation.operations

import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.UserPick

data class OperationHistoryState(
    val isLoading: Boolean = false,
    val operations: List<StockOperation> = emptyList(),
    val typeFilter: StockOperationType? = null,
    val products: List<Product> = emptyList(),
    val filterUsers: List<UserPick> = emptyList(),
    val selectedProductId: Long? = null,
    val selectedUserId: Long? = null,
    val dateFromInput: String = "",
    val dateToInput: String = "",
    val errorMessage: String? = null,
)
