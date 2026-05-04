package com.example.warehouse_accounting_app.presentation.operations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.repository.OperationsFilter
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductsUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.GetOperationHistoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.GetUsersForOperationFiltersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OperationHistoryViewModel(
    private val getOperationHistoryUseCase: GetOperationHistoryUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getUsersForOperationFiltersUseCase: GetUsersForOperationFiltersUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(OperationHistoryState())
    val state = _state.asStateFlow()

    init {
        loadFilterLists()
        fetchWithCurrentInputs()
    }

    fun onEvent(event: OperationHistoryEvent) {
        when (event) {
            OperationHistoryEvent.Refresh -> fetchWithCurrentInputs()
            is OperationHistoryEvent.TypeFilterChanged -> {
                _state.update { it.copy(typeFilter = event.type) }
                fetchWithCurrentInputs()
            }
            is OperationHistoryEvent.ProductFilterChanged ->
                _state.update { it.copy(selectedProductId = event.productId) }
            is OperationHistoryEvent.UserFilterChanged ->
                _state.update { it.copy(selectedUserId = event.userId) }
            is OperationHistoryEvent.DateFromChanged ->
                _state.update { it.copy(dateFromInput = event.value) }
            is OperationHistoryEvent.DateToChanged ->
                _state.update { it.copy(dateToInput = event.value) }
            OperationHistoryEvent.ApplyFilters -> fetchWithCurrentInputs()
        }
    }

    fun load() = onEvent(OperationHistoryEvent.Refresh)

    private fun loadFilterLists() {
        viewModelScope.launch {
            val productsRes = getProductsUseCase(search = null, categoryId = null, activeOnly = true)
            val usersRes = getUsersForOperationFiltersUseCase()
            val products = if (productsRes is AppResult.Success) productsRes.data else emptyList()
            val users = if (usersRes is AppResult.Success) usersRes.data else emptyList()
            _state.update { it.copy(products = products, filterUsers = users) }
        }
    }

    private fun fetchWithCurrentInputs() {
        viewModelScope.launch {
            val s = _state.value
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val filter =
                OperationsFilter(
                    type = s.typeFilter,
                    productId = s.selectedProductId,
                    userId = s.selectedUserId,
                    dateFrom = s.dateFromInput.trim().takeIf { it.isNotEmpty() },
                    dateTo = s.dateToInput.trim().takeIf { it.isNotEmpty() },
                )
            when (val r = getOperationHistoryUseCase(filter)) {
                is AppResult.Success ->
                    _state.update { it.copy(isLoading = false, operations = r.data) }
                is AppResult.Error ->
                    _state.update { it.copy(isLoading = false, errorMessage = r.message) }
            }
        }
    }
}
