package com.example.warehouse_accounting_app.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.presentation.common.toUserMessage
import com.example.warehouse_accounting_app.domain.usecase.category.GetCategoriesUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.CreateProductUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductDetailsUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.UpdateProductUseCase
import com.example.warehouse_accounting_app.core.util.NumberFormatters
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductEditViewModel(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductEditState())
    val state = _state.asStateFlow()

    private val _events = Channel<ProductEditEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init { loadCategories() }

    private fun loadCategories() {
        viewModelScope.launch {
            when (val r = getCategoriesUseCase()) {
                is AppResult.Success -> _state.update { it.copy(categories = r.data) }
                is AppResult.Error -> {}
            }
        }
    }

    private fun userVisibleLoadError(r: AppResult.Error): String =
        when (r.appError) {
            is AppError.NotFound -> r.appError.toUserMessage("Товар не найден")
            else -> r.toUserMessage("Не удалось загрузить товар")
        }

    private fun userVisibleSaveError(r: AppResult.Error): String =
        when (r.appError) {
            is AppError.NotFound -> r.appError.toUserMessage("Товар не найден")
            else -> r.toUserMessage()
        }

    fun loadProduct(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val r = getProductDetailsUseCase(id)) {
                is AppResult.Success -> {
                    val p = r.data
                    _state.update {
                        it.copy(
                            isLoading = false, editingProduct = p,
                            article = p.article, name = p.name,
                            selectedCategoryId = p.categoryId, unit = p.unit,
                            purchasePrice = NumberFormatters.editableDecimal(p.purchasePrice, 2),
                            salePrice = NumberFormatters.editableDecimal(p.salePrice, 2),
                            minStock = NumberFormatters.editableDecimal(p.minStock, 3),
                            isActive = p.isActive,
                        )
                    }
                }
                is AppResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = userVisibleLoadError(r)) }
                }
            }
        }
    }

    fun onArticleChange(v: String) = _state.update { it.copy(article = v, articleError = null) }
    fun onNameChange(v: String) = _state.update { it.copy(name = v, nameError = null) }
    fun onCategoryChange(id: Long?) = _state.update { it.copy(selectedCategoryId = id, categoryError = null) }
    fun onUnitChange(v: String) = _state.update { it.copy(unit = v, unitError = null) }
    fun onPurchasePriceChange(v: String) = _state.update { it.copy(purchasePrice = v, purchasePriceError = null) }
    fun onSalePriceChange(v: String) = _state.update { it.copy(salePrice = v, salePriceError = null) }
    fun onMinStockChange(v: String) = _state.update { it.copy(minStock = v, minStockError = null) }
    fun onIsActiveChange(v: Boolean) = _state.update { it.copy(isActive = v) }

    fun onSave() {
        val s = _state.value
        var hasError = false

        val article = s.article.trim()
        val name = s.name.trim()
        val unit = s.unit.trim()
        val pp = NumberFormatters.parseUserDecimal(s.purchasePrice)
        val sp = NumberFormatters.parseUserDecimal(s.salePrice)
        val ms = NumberFormatters.parseUserDecimal(s.minStock)

        if (article.isBlank()) { _state.update { it.copy(articleError = "Артикул не может быть пустым") }; hasError = true }
        if (name.isBlank()) { _state.update { it.copy(nameError = "Название не может быть пустым") }; hasError = true }
        if (s.selectedCategoryId == null) { _state.update { it.copy(categoryError = "Выберите категорию") }; hasError = true }
        if (unit.isBlank()) { _state.update { it.copy(unitError = "Единица измерения не может быть пустой") }; hasError = true }
        if (pp == null || pp < 0) { _state.update { it.copy(purchasePriceError = "Введите цену ≥ 0") }; hasError = true }
        if (sp == null || sp < 0) { _state.update { it.copy(salePriceError = "Введите цену ≥ 0") }; hasError = true }
        if (ms == null || ms < 0) { _state.update { it.copy(minStockError = "Введите значение ≥ 0") }; hasError = true }
        if (hasError) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val result = if (s.isEditMode) {
                updateProductUseCase(s.editingProduct!!.id, article, name, s.selectedCategoryId!!, unit, pp!!, sp!!, ms!!, s.isActive)
            } else {
                createProductUseCase(article, name, s.selectedCategoryId!!, unit, pp!!, sp!!, ms!!)
            }
            when (result) {
                is AppResult.Success -> {
                    _state.update { it.copy(isSaving = false) }
                    _events.send(ProductEditEvent.SaveSuccess)
                }
                is AppResult.Error -> {
                    val msg = userVisibleSaveError(result)
                    _state.update { it.copy(isSaving = false, errorMessage = msg) }
                    _events.send(ProductEditEvent.ShowError(msg))
                }
            }
        }
    }
}
