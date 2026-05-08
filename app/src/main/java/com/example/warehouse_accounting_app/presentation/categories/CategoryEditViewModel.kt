package com.example.warehouse_accounting_app.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.usecase.category.CreateCategoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.GetCategoryByIdUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.UpdateCategoryUseCase
import com.example.warehouse_accounting_app.presentation.common.toUserMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                is AppResult.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.toUserMessage()) }
            }
        }
    }

    fun onNameChange(value: String) {
        _state.update { it.copy(name = value, nameError = null) }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value) }
    }

    fun onIsActiveChange(value: Boolean) {
        _state.update { it.copy(isActive = value) }
    }

    fun onSave() {
        val name = _state.value.name.trim()
        if (name.isBlank()) {
            _state.update { it.copy(nameError = "Название не может быть пустым") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val s = _state.value
            val result =
                if (s.isEditMode) {
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
                    val msg = result.toUserMessage()
                    _state.update { it.copy(isSaving = false, errorMessage = msg) }
                    _events.send(CategoryEditEvent.ShowError(msg))
                }
            }
        }
    }
}
