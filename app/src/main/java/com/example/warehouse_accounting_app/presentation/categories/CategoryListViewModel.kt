package com.example.warehouse_accounting_app.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.model.Category
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.DeleteCategoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.GetCategoriesUseCase
import com.example.warehouse_accounting_app.presentation.common.toUserMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryListViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryListState())
    val state = _state.asStateFlow()

    private val _events = Channel<CategoryListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var currentUserRole: UserRole? = null

    init {
        loadUserRole()
        loadCategories()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is AppResult.Success -> currentUserRole = result.data.role
                is AppResult.Error -> {
                    when (result.appError) {
                        is AppError.SessionExpired,
                        is AppError.Unauthorized,
                        -> _events.send(CategoryListEvent.SessionExpired)
                        else -> Unit
                    }
                }
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getCategoriesUseCase(activeOnly = true)) {
                is AppResult.Success -> _state.update { it.copy(isLoading = false, categories = result.data) }
                is AppResult.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.toUserMessage()) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onDeleteCategory(category: Category) {
        viewModelScope.launch {
            when (val result = deleteCategoryUseCase(category.id)) {
                is AppResult.Success -> {
                    _state.update { state ->
                        state.copy(
                            categories = state.categories.map { c ->
                                if (c.id == result.data.id) result.data else c
                            },
                        )
                    }
                    _events.send(CategoryListEvent.ShowSuccess("Категория «${result.data.name}» деактивирована"))
                }
                is AppResult.Error -> _events.send(CategoryListEvent.ShowError(result.toUserMessage()))
            }
        }
    }

    fun isAdmin(): Boolean = currentUserRole == UserRole.ADMIN
    fun getUserRole(): UserRole? = currentUserRole

    fun onCreateClick() {
        viewModelScope.launch { _events.send(CategoryListEvent.NavigateToCreate) }
    }

    fun onEditClick(id: Long) {
        viewModelScope.launch { _events.send(CategoryListEvent.NavigateToEdit(id)) }
    }
}
