package com.example.warehouse_accounting_app.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.GetCategoriesUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.DeleteProductUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductsUseCase
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
            _state.update { it.copy(isRoleLoading = true, roleErrorMessage = null) }
            when (val result = getCurrentUserUseCase()) {
                is AppResult.Success -> {
                    currentUserRole = result.data.role
                    _state.update {
                        it.copy(
                            isRoleLoading = false,
                            roleErrorMessage = null,
                            isAdminUser = result.data.role == UserRole.ADMIN,
                        )
                    }
                }
                is AppResult.Error -> {
                    currentUserRole = null
                    _state.update {
                        it.copy(
                            isRoleLoading = false,
                            roleErrorMessage = "Не удалось определить права пользователя",
                            isAdminUser = false,
                        )
                    }
                }
            }
        }
    }

    fun retryUserRole() = loadUserRole()

    private fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isCategoriesLoading = true, categoriesErrorMessage = null) }
            when (val result = getCategoriesUseCase()) {
                is AppResult.Success -> _state.update {
                    it.copy(
                        isCategoriesLoading = false,
                        categories = result.data,
                        categoriesErrorMessage = null,
                    )
                }
                is AppResult.Error -> _state.update {
                    it.copy(
                        isCategoriesLoading = false,
                        categories = emptyList(),
                        categoriesErrorMessage = result.message.ifBlank { "Не удалось загрузить категории" },
                        selectedCategoryId = null,
                    )
                }
            }
        }
    }

    fun retryCategories() = loadCategories()

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

    fun isAdmin() = _state.value.isAdminUser
    fun getUserRole() = currentUserRole
    fun onCreateClick() = viewModelScope.launch { _events.send(ProductListEvent.NavigateToCreate) }
    fun onEditClick(id: Long) = viewModelScope.launch { _events.send(ProductListEvent.NavigateToEdit(id)) }
    fun onDetailsClick(id: Long) = viewModelScope.launch { _events.send(ProductListEvent.NavigateToDetails(id)) }
}
