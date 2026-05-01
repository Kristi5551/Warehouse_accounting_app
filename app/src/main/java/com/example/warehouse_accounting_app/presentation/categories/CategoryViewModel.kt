package com.example.warehouse_accounting_app.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Category
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.CreateCategoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.DeleteCategoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.GetCategoriesUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.GetCategoryByIdUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.UpdateCategoryUseCase
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
                    if (result.throwable?.message?.contains("401") == true ||
                        result.throwable?.message?.contains("403") == true
                    ) _events.send(CategoryListEvent.SessionExpired)
                }
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getCategoriesUseCase(activeOnly = true)) {
                is AppResult.Success -> _state.update { it.copy(isLoading = false, categories = result.data) }
                is AppResult.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
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
                is AppResult.Error -> _events.send(CategoryListEvent.ShowError(result.message))
            }
        }
    }

    fun isAdmin(): Boolean = currentUserRole == UserRole.ADMIN
    fun getUserRole(): UserRole? = currentUserRole

    fun onCreateClick() { viewModelScope.launch { _events.send(CategoryListEvent.NavigateToCreate) } }
    fun onEditClick(id: Long) { viewModelScope.launch { _events.send(CategoryListEvent.NavigateToEdit(id)) } }
}

class CategoryEditViewModel(
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryEditState())
    val state = _state.asStateFlow()

    private val _events = Channel<CategoryEditEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun loadCategory(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getCategoryByIdUseCase(id)) {
                is AppResult.Success -> {
                    val c = result.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            editingCategory = c,
                            name = c.name,
                            description = c.description ?: "",
                            isActive = c.isActive,
                        )
                    }
                }
                is AppResult.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun onNameChange(value: String) { _state.update { it.copy(name = value, nameError = null) } }
    fun onDescriptionChange(value: String) { _state.update { it.copy(description = value) } }
    fun onIsActiveChange(value: Boolean) { _state.update { it.copy(isActive = value) } }

    fun onSave() {
        val name = _state.value.name.trim()
        if (name.isBlank()) {
            _state.update { it.copy(nameError = "Название не может быть пустым") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val s = _state.value
            val result = if (s.isEditMode) {
                updateCategoryUseCase(s.editingCategory!!.id, name, s.description.trim().ifBlank { null }, s.isActive)
            } else {
                createCategoryUseCase(name, s.description.trim().ifBlank { null })
            }
            when (result) {
                is AppResult.Success -> {
                    _state.update { it.copy(isSaving = false) }
                    _events.send(CategoryEditEvent.SaveSuccess)
                }
                is AppResult.Error -> {
                    _state.update { it.copy(isSaving = false, errorMessage = result.message) }
                    _events.send(CategoryEditEvent.ShowError(result.message))
                }
            }
        }
    }
}
