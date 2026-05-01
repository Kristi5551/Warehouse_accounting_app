package com.example.warehouse_accounting_app.presentation.operations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.reports.OperationReport
import com.example.warehouse_accounting_app.domain.usecase.report.GetOperationsReportUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OperationHistoryState(
    val isLoading: Boolean = false,
    val operations: List<OperationReport> = emptyList(),
    val typeFilter: StockOperationType? = null,
    val searchQuery: String = "",
    val errorMessage: String? = null,
)

val OperationHistoryState.filtered: List<OperationReport>
    get() {
        var list = operations
        if (typeFilter != null) list = list.filter { it.operationType == typeFilter }
        if (searchQuery.isNotBlank()) list = list.filter {
            it.productName.contains(searchQuery, ignoreCase = true) ||
            it.productArticle.contains(searchQuery, ignoreCase = true) ||
            it.createdByName.contains(searchQuery, ignoreCase = true)
        }
        return list
    }

class OperationHistoryViewModel(
    private val getOperationsReportUseCase: GetOperationsReportUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(OperationHistoryState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val r = getOperationsReportUseCase(null, null, null, null, null)
            if (r is AppResult.Success) {
                val data = r.data
                _state.update { it.copy(isLoading = false, operations = data) }
            } else if (r is AppResult.Error) {
                val msg = r.message
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    fun onTypeFilter(type: StockOperationType?) = _state.update { it.copy(typeFilter = type) }
    fun onSearchChange(q: String) = _state.update { it.copy(searchQuery = q) }
}
