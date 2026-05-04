package com.example.warehouse_accounting_app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getCurrentUser()) {
                is AppResult.Success ->
                    _state.update { it.copy(user = result.data, isLoading = false, errorMessage = null) }
                is AppResult.Error -> {
                    if (result.appError is AppError.Unauthorized) {
                        logoutUseCase()
                        _state.update { it.copy(isLoading = false, sessionExpired = true) }
                    } else {
                        _state.update {
                            it.copy(
                                user = null,
                                isLoading = false,
                                errorMessage = result.message,
                            )
                        }
                    }
                }
            }
        }
    }

    fun onLogout(onDone: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onDone()
        }
    }

    fun refresh() = load()
}
