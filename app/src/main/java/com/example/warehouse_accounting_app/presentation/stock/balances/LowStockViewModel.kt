package com.example.warehouse_accounting_app.presentation.stock.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.usecase.stock.GetLowStockUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LowStockViewModel(
    private val getLowStockUseCase: GetLowStockUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(StockBalanceState())
    val state = _state.asStateFlow()

    init {
        load(initial = true)
    }

    fun onEvent(event: StockBalanceEvent) {
        when (event) {
            is StockBalanceEvent.SearchChanged -> _state.update { it.copy(searchQuery = event.value) }
            is StockBalanceEvent.StatusFilterChanged -> Unit
            StockBalanceEvent.Refresh -> load(refresh = true)
        }
    }

    fun load(initial: Boolean = false, refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _state.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
            }
            when (val r = getLowStockUseCase()) {
                is AppResult.Success -> {
                    val data = r.data
                    _state.update {
                        it.copy(isLoading = false, isRefreshing = false, balances = data)
                    }
                }
                is AppResult.Error -> {
                    val msg = r.message
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = msg,
                            balances = if (initial) emptyList() else it.balances,
                        )
                    }
                }
            }
        }
    }
}
