package com.example.warehouse_accounting_app.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val login: LoginUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent, onSuccess: () -> Unit = {}, onRegister: () -> Unit = {}) {
        when (event) {
            is LoginEvent.EmailChanged -> _state.update { it.copy(email = event.value, errorMessage = null) }
            is LoginEvent.PasswordChanged -> _state.update { it.copy(password = event.value, errorMessage = null) }
            LoginEvent.RegisterNavigation -> onRegister()
            LoginEvent.Submit -> submit(onSuccess)
        }
    }

    private fun submit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val email = _state.value.email
            val password = _state.value.password
            when (val result = login(email, password)) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                is AppResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
}
