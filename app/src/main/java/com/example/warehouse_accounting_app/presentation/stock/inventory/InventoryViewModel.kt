package com.example.warehouse_accounting_app.presentation.stock.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.presentation.common.toUserMessage
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductsUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.CreateInventoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.GetStockBalancesUseCase
import com.example.warehouse_accounting_app.core.util.NumberFormatters
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val DEFAULT_WAREHOUSE_ID = 1L

class InventoryViewModel(
    private val createInventoryUseCase: CreateInventoryUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getStockBalancesUseCase: GetStockBalancesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(InventoryState())
    val state = _state.asStateFlow()
    private val _events = Channel<InventoryEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val r = getProductsUseCase(activeOnly = true)) {
                is AppResult.Success ->
                    _state.update { it.copy(isLoading = false, products = r.data) }
                is AppResult.Error ->
                    _state.update { it.copy(isLoading = false, errorMessage = r.toUserMessage()) }
            }
        }
    }

    fun onProductSelect(p: Product?) {
        _state.update {
            it.copy(
                selectedProduct = p,
                productError = null,
                bookedQuantity = null,
                actualQuantity = "",
                actualError = null,
            )
        }
        if (p == null) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingBalance = true) }
            when (val r = getStockBalancesUseCase(null, null, null)) {
                is AppResult.Success -> {
                    val row = r.data.firstOrNull {
                        it.productId == p.id && it.warehouseId == DEFAULT_WAREHOUSE_ID
                    }
                    _state.update {
                        it.copy(isLoadingBalance = false, bookedQuantity = row?.quantity ?: 0.0)
                    }
                }
                is AppResult.Error -> {
                    _state.update {
                        it.copy(isLoadingBalance = false, bookedQuantity = 0.0, errorMessage = r.toUserMessage())
                    }
                }
            }
        }
    }

    fun onActualQuantityChange(v: String) = _state.update { it.copy(actualQuantity = v, actualError = null) }
    fun onCommentChange(v: String) = _state.update { it.copy(comment = v) }

    fun onSubmit() {
        val s = _state.value
        var err = false
        if (s.selectedProduct == null) {
            _state.update { it.copy(productError = "Выберите товар") }
            err = true
        }
        val actual = NumberFormatters.parseUserDecimal(s.actualQuantity)
        if (actual == null || actual < 0) {
            _state.update { it.copy(actualError = "Введите фактический остаток ≥ 0") }
            err = true
        }
        if (err) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            when (
                val result = createInventoryUseCase(
                    DEFAULT_WAREHOUSE_ID,
                    s.selectedProduct!!.id,
                    actual!!,
                    s.comment.trim().ifBlank { null },
                )
            ) {
                is AppResult.Success -> {
                    _state.update { it.copy(isSaving = false) }
                    _events.send(InventoryEvent.Success)
                }
                is AppResult.Error -> {
                    val msg = result.toUserMessage()
                    _state.update { it.copy(isSaving = false, errorMessage = msg) }
                    _events.send(InventoryEvent.Error(msg))
                }
            }
        }
    }
}
