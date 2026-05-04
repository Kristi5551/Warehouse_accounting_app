package com.example.warehouse_accounting_app.presentation.stock.issue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductsUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.CreateIssueUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val DEFAULT_WAREHOUSE_ID = 1L

class IssueViewModel(
    private val createIssueUseCase: CreateIssueUseCase,
    private val getProductsUseCase: GetProductsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(IssueState())
    val state = _state.asStateFlow()
    private val _events = Channel<IssueEvent>(Channel.BUFFERED)
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
                    _state.update { it.copy(isLoading = false, errorMessage = r.message) }
            }
        }
    }

    fun onProductSelect(p: Product?) = _state.update { it.copy(selectedProduct = p, productError = null) }
    fun onQuantityChange(v: String) = _state.update { it.copy(quantity = v, quantityError = null) }
    fun onReasonChange(v: String) = _state.update { it.copy(reason = v) }
    fun onCommentChange(v: String) = _state.update { it.copy(comment = v) }

    fun onSubmit() {
        val s = _state.value
        var err = false
        if (s.selectedProduct == null) {
            _state.update { it.copy(productError = "Выберите товар") }
            err = true
        }
        val qty = s.quantity.trim().toDoubleOrNull()
        if (qty == null || qty <= 0) {
            _state.update { it.copy(quantityError = "Введите количество > 0") }
            err = true
        }
        if (err) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            when (
                val result = createIssueUseCase(
                    DEFAULT_WAREHOUSE_ID,
                    s.selectedProduct!!.id,
                    qty!!,
                    s.reason.trim().ifBlank { null },
                    s.comment.trim().ifBlank { null },
                )
            ) {
                is AppResult.Success -> {
                    _state.update { it.copy(isSaving = false) }
                    _events.send(IssueEvent.Success)
                }
                is AppResult.Error -> {
                    val msg = result.message
                    _state.update { it.copy(isSaving = false, errorMessage = msg) }
                    _events.send(IssueEvent.Error(msg))
                }
            }
        }
    }
}
