package com.example.warehouse_accounting_app.presentation.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductsUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.CreateInventoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.CreateIssueUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.CreateReceiptUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.CreateWriteOffUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StockOperationState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val products: List<Product> = emptyList(),
    val selectedProduct: Product? = null,
    val quantity: String = "",
    val price: String = "",
    val supplier: String = "",
    val reason: String = "",
    val comment: String = "",
    val productError: String? = null,
    val quantityError: String? = null,
    val priceError: String? = null,
    val errorMessage: String? = null,
)

sealed interface StockOperationEvent {
    data object Success : StockOperationEvent
    data class Error(val message: String) : StockOperationEvent
}

private const val DEFAULT_WAREHOUSE_ID = 1L

class ReceiptViewModel(
    private val createReceiptUseCase: CreateReceiptUseCase,
    private val getProductsUseCase: GetProductsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(StockOperationState())
    val state = _state.asStateFlow()
    private val _events = Channel<StockOperationEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init { loadProducts() }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val r = getProductsUseCase(activeOnly = true)
            if (r is AppResult.Success) {
                val data = r.data
                _state.update { it.copy(isLoading = false, products = data) }
            } else if (r is AppResult.Error) {
                val msg = r.message
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    fun onProductSelect(p: Product?) = _state.update { it.copy(selectedProduct = p, productError = null) }
    fun onQuantityChange(v: String) = _state.update { it.copy(quantity = v, quantityError = null) }
    fun onPriceChange(v: String) = _state.update { it.copy(price = v, priceError = null) }
    fun onSupplierChange(v: String) = _state.update { it.copy(supplier = v) }
    fun onCommentChange(v: String) = _state.update { it.copy(comment = v) }

    fun onSubmit() {
        val s = _state.value
        var err = false
        if (s.selectedProduct == null) { _state.update { it.copy(productError = "Выберите товар") }; err = true }
        val qty = s.quantity.trim().toDoubleOrNull()
        if (qty == null || qty <= 0) { _state.update { it.copy(quantityError = "Введите количество > 0") }; err = true }
        val price = s.price.trim().toDoubleOrNull()
        if (price == null || price < 0) { _state.update { it.copy(priceError = "Введите цену ≥ 0") }; err = true }
        if (err) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val result = createReceiptUseCase(DEFAULT_WAREHOUSE_ID, s.selectedProduct!!.id, qty!!, price!!, s.supplier.trim().ifBlank { null }, s.comment.trim().ifBlank { null })
            if (result is AppResult.Success) {
                _state.update { it.copy(isSaving = false) }
                _events.send(StockOperationEvent.Success)
            } else if (result is AppResult.Error) {
                val msg = result.message
                _state.update { it.copy(isSaving = false, errorMessage = msg) }
                _events.send(StockOperationEvent.Error(msg))
            }
        }
    }
}

class IssueViewModel(
    private val createIssueUseCase: CreateIssueUseCase,
    private val getProductsUseCase: GetProductsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(StockOperationState())
    val state = _state.asStateFlow()
    private val _events = Channel<StockOperationEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init { loadProducts() }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val r = getProductsUseCase(activeOnly = true)
            if (r is AppResult.Success) {
                val data = r.data
                _state.update { it.copy(isLoading = false, products = data) }
            } else if (r is AppResult.Error) {
                val msg = r.message
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
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
        if (s.selectedProduct == null) { _state.update { it.copy(productError = "Выберите товар") }; err = true }
        val qty = s.quantity.trim().toDoubleOrNull()
        if (qty == null || qty <= 0) { _state.update { it.copy(quantityError = "Введите количество > 0") }; err = true }
        if (err) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val result = createIssueUseCase(DEFAULT_WAREHOUSE_ID, s.selectedProduct!!.id, qty!!, s.reason.trim().ifBlank { null }, s.comment.trim().ifBlank { null })
            if (result is AppResult.Success) {
                _state.update { it.copy(isSaving = false) }
                _events.send(StockOperationEvent.Success)
            } else if (result is AppResult.Error) {
                val msg = result.message
                _state.update { it.copy(isSaving = false, errorMessage = msg) }
                _events.send(StockOperationEvent.Error(msg))
            }
        }
    }
}

class WriteOffViewModel(
    private val createWriteOffUseCase: CreateWriteOffUseCase,
    private val getProductsUseCase: GetProductsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(StockOperationState())
    val state = _state.asStateFlow()
    private val _events = Channel<StockOperationEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init { loadProducts() }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val r = getProductsUseCase(activeOnly = true)
            if (r is AppResult.Success) {
                val data = r.data
                _state.update { it.copy(isLoading = false, products = data) }
            } else if (r is AppResult.Error) {
                val msg = r.message
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
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
        if (s.selectedProduct == null) { _state.update { it.copy(productError = "Выберите товар") }; err = true }
        val qty = s.quantity.trim().toDoubleOrNull()
        if (qty == null || qty <= 0) { _state.update { it.copy(quantityError = "Введите количество > 0") }; err = true }
        if (err) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val result = createWriteOffUseCase(DEFAULT_WAREHOUSE_ID, s.selectedProduct!!.id, qty!!, s.reason.trim().ifBlank { null }, s.comment.trim().ifBlank { null })
            if (result is AppResult.Success) {
                _state.update { it.copy(isSaving = false) }
                _events.send(StockOperationEvent.Success)
            } else if (result is AppResult.Error) {
                val msg = result.message
                _state.update { it.copy(isSaving = false, errorMessage = msg) }
                _events.send(StockOperationEvent.Error(msg))
            }
        }
    }
}

class InventoryViewModel(
    private val createInventoryUseCase: CreateInventoryUseCase,
    private val getProductsUseCase: GetProductsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(StockOperationState())
    val state = _state.asStateFlow()
    private val _events = Channel<StockOperationEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init { loadProducts() }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val r = getProductsUseCase(activeOnly = true)
            if (r is AppResult.Success) {
                val data = r.data
                _state.update { it.copy(isLoading = false, products = data) }
            } else if (r is AppResult.Error) {
                val msg = r.message
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    fun onProductSelect(p: Product?) = _state.update { it.copy(selectedProduct = p, productError = null) }
    fun onQuantityChange(v: String) = _state.update { it.copy(quantity = v, quantityError = null) }
    fun onCommentChange(v: String) = _state.update { it.copy(comment = v) }

    fun onSubmit() {
        val s = _state.value
        var err = false
        if (s.selectedProduct == null) { _state.update { it.copy(productError = "Выберите товар") }; err = true }
        val qty = s.quantity.trim().toDoubleOrNull()
        if (qty == null || qty < 0) { _state.update { it.copy(quantityError = "Введите фактическое количество ≥ 0") }; err = true }
        if (err) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val result = createInventoryUseCase(DEFAULT_WAREHOUSE_ID, s.selectedProduct!!.id, qty!!, s.comment.trim().ifBlank { null })
            if (result is AppResult.Success) {
                _state.update { it.copy(isSaving = false) }
                _events.send(StockOperationEvent.Success)
            } else if (result is AppResult.Error) {
                val msg = result.message
                _state.update { it.copy(isSaving = false, errorMessage = msg) }
                _events.send(StockOperationEvent.Error(msg))
            }
        }
    }
}
