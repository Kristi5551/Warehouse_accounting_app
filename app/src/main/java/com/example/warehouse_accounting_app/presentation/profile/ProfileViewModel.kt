package com.example.warehouse_accounting_app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val r = getCurrentUser()) {
                is AppResult.Success ->
                    _state.update { it.copy(user = r.data, isLoading = false) }
                is AppResult.Error ->
                    _state.update { it.copy(isLoading = false, errorMessage = r.message) }
            }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onDone()
        }
    }
}
