package com.example.warehouse_accounting_app.presentation.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockStatus
import com.example.warehouse_accounting_app.domain.usecase.stock.GetLowStockUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.GetStockBalancesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StockBalanceState(
    val isLoading: Boolean = false,
    val balances: List<StockBalance> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: StockStatus? = null,
    val errorMessage: String? = null,
)

val StockBalanceState.filtered: List<StockBalance>
    get() {
        var list = balances
        if (searchQuery.isNotBlank())
            list = list.filter {
                it.productName.contains(searchQuery, ignoreCase = true) ||
                it.productArticle.contains(searchQuery, ignoreCase = true)
            }
        if (statusFilter != null) list = list.filter { it.status == statusFilter }
        return list
    }

class StockBalanceViewModel(
    private val getStockBalancesUseCase: GetStockBalancesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(StockBalanceState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val r = getStockBalancesUseCase(null)
            if (r is AppResult.Success) {
                val data = r.data
                _state.update { it.copy(isLoading = false, balances = data) }
            } else if (r is AppResult.Error) {
                val msg = r.message
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    fun onSearchChange(q: String) = _state.update { it.copy(searchQuery = q) }
    fun onStatusFilter(s: StockStatus?) = _state.update { it.copy(statusFilter = s) }
}

class LowStockViewModel(
    private val getLowStockUseCase: GetLowStockUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(StockBalanceState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val r = getLowStockUseCase(null)
            if (r is AppResult.Success) {
                val data = r.data
                _state.update { it.copy(isLoading = false, balances = data) }
            } else if (r is AppResult.Error) {
                val msg = r.message
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    fun onSearchChange(q: String) = _state.update { it.copy(searchQuery = q) }
}
