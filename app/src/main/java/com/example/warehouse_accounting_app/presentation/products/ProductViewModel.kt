package com.example.warehouse_accounting_app.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.GetCategoriesUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.CreateProductUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.DeleteProductUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductDetailsUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductsUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.UpdateProductUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductListState())
    val state = _state.asStateFlow()

    private val _events = Channel<ProductListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var currentUserRole: UserRole? = null

    init {
        loadUserRole()
        loadCategories()
        loadProducts()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            if (getCurrentUserUseCase() is AppResult.Success) {
                currentUserRole = (getCurrentUserUseCase() as? AppResult.Success)?.data?.role
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            if (getCategoriesUseCase() is AppResult.Success)
                _state.update { it.copy(categories = (getCategoriesUseCase() as AppResult.Success).data) }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val s = _state.value
            when (val result = getProductsUseCase(activeOnly = s.activeOnly)) {
                is AppResult.Success -> _state.update { it.copy(isLoading = false, products = result.data) }
                is AppResult.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun onSearchChange(q: String) = _state.update { it.copy(searchQuery = q) }
    fun onCategoryFilter(id: Long?) = _state.update { it.copy(selectedCategoryId = id) }
    fun onActiveOnlyChange(v: Boolean) { _state.update { it.copy(activeOnly = v) }; loadProducts() }

    fun onDeleteProduct(product: Product) {
        viewModelScope.launch {
            when (val r = deleteProductUseCase(product.id)) {
                is AppResult.Success -> {
                    _state.update { s -> s.copy(products = s.products.map { if (it.id == r.data.id) r.data else it }) }
                    _events.send(ProductListEvent.ShowSuccess("Товар «${r.data.name}» деактивирован"))
                }
                is AppResult.Error -> _events.send(ProductListEvent.ShowError(r.message))
            }
        }
    }

    fun isAdmin() = currentUserRole == UserRole.ADMIN
    fun getUserRole() = currentUserRole
    fun onCreateClick() = viewModelScope.launch { _events.send(ProductListEvent.NavigateToCreate) }
    fun onEditClick(id: Long) = viewModelScope.launch { _events.send(ProductListEvent.NavigateToEdit(id)) }
    fun onDetailsClick(id: Long) = viewModelScope.launch { _events.send(ProductListEvent.NavigateToDetails(id)) }
}

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
                            purchasePrice = "%.2f".format(p.purchasePrice),
                            salePrice = "%.2f".format(p.salePrice),
                            minStock = "%.3f".format(p.minStock),
                            isActive = p.isActive,
                        )
                    }
                }
                is AppResult.Error -> _state.update { it.copy(isLoading = false, errorMessage = r.message) }
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
        val pp = s.purchasePrice.trim().toDoubleOrNull()
        val sp = s.salePrice.trim().toDoubleOrNull()
        val ms = s.minStock.trim().toDoubleOrNull()

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
                is AppResult.Success -> { _state.update { it.copy(isSaving = false) }; _events.send(ProductEditEvent.SaveSuccess) }
                is AppResult.Error -> { _state.update { it.copy(isSaving = false, errorMessage = result.message) }; _events.send(ProductEditEvent.ShowError(result.message)) }
            }
        }
    }
}
