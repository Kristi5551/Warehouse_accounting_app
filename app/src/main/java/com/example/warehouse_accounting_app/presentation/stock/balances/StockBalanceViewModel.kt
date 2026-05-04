package com.example.warehouse_accounting_app.presentation.stock.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockStatus
import com.example.warehouse_accounting_app.domain.usecase.stock.GetStockBalancesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StockBalanceViewModel(
    private val getStockBalancesUseCase: GetStockBalancesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(StockBalanceState())
    val state = _state.asStateFlow()

    private var searchDebounce: Job? = null

    init {
        load(initial = true)
    }

    fun onEvent(event: StockBalanceEvent) {
        when (event) {
            is StockBalanceEvent.SearchChanged -> {
                _state.update { it.copy(searchQuery = event.value) }
                searchDebounce?.cancel()
                searchDebounce = viewModelScope.launch {
                    delay(400)
                    load()
                }
            }
            is StockBalanceEvent.StatusFilterChanged -> {
                _state.update { it.copy(statusFilter = event.status) }
                load()
            }
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
            val s = _state.value
            val r = getStockBalancesUseCase(
                search = s.searchQuery.trim().takeIf { it.isNotEmpty() },
                categoryId = null,
                status = s.statusFilter,
            )
            when (r) {
                is AppResult.Success -> {
                    val data = r.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            balances = data,
                        )
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
