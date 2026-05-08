package com.example.warehouse_accounting_app.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.StockHistoryFilter
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.presentation.common.toUserMessage
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductDetailsUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.GetProductHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val getProductHistoryUseCase: GetProductHistoryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsState())
    val state = _state.asStateFlow()

    private fun userVisibleProductError(r: AppResult.Error): String =
        r.toUserMessage("Не удалось загрузить товар")

    private fun userVisibleHistoryError(r: AppResult.Error): String =
        r.toUserMessage("Не удалось загрузить историю товара")

    fun loadProduct(productId: Long) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isProductLoading = true,
                    product = null,
                    productErrorMessage = null,
                    history = emptyList(),
                    historyErrorMessage = null,
                    isHistoryLoading = false,
                )
            }
            val isAdmin = (getCurrentUserUseCase() as? AppResult.Success)?.data?.role == UserRole.ADMIN
            when (val r = getProductDetailsUseCase(productId)) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            isProductLoading = false,
                            product = r.data,
                            productErrorMessage = null,
                            isAdmin = isAdmin,
                        )
                    }
                    loadHistoryInternal(productId)
                }
                is AppResult.Error ->
                    _state.update {
                        it.copy(
                            isProductLoading = false,
                            productErrorMessage = userVisibleProductError(r),
                            product = null,
                            isAdmin = isAdmin,
                        )
                    }
            }
        }
    }

    fun retryHistory() {
        val id = _state.value.product?.id ?: return
        viewModelScope.launch { loadHistoryInternal(id) }
    }

    private suspend fun loadHistoryInternal(productId: Long) {
        _state.update { it.copy(isHistoryLoading = true, historyErrorMessage = null) }
        when (val r = getProductHistoryUseCase(productId, StockHistoryFilter())) {
            is AppResult.Success ->
                _state.update {
                    it.copy(
                        isHistoryLoading = false,
                        history = r.data.take(10),
                        historyErrorMessage = null,
                    )
                }
            is AppResult.Error ->
                _state.update {
                    it.copy(
                        isHistoryLoading = false,
                        historyErrorMessage = userVisibleHistoryError(r),
                    )
                }
        }
    }
}
