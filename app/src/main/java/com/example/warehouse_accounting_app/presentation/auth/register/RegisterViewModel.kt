package com.example.warehouse_accounting_app.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val register: RegisterUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.FullNameChanged ->
                _state.update { it.copy(fullName = event.value, errorMessage = null, successMessage = null) }
            is RegisterEvent.EmailChanged ->
                _state.update { it.copy(email = event.value, errorMessage = null, successMessage = null) }
            is RegisterEvent.PasswordChanged ->
                _state.update { it.copy(password = event.value, errorMessage = null, successMessage = null) }
            is RegisterEvent.RepeatPasswordChanged ->
                _state.update { it.copy(repeatPassword = event.value, errorMessage = null, successMessage = null) }
            is RegisterEvent.RoleChanged ->
                _state.update { it.copy(selectedRole = event.role) }
            RegisterEvent.Submit -> submit()
        }
    }

    private fun submit() {
        viewModelScope.launch {
            val s = _state.value
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (
                val result = register(
                    fullName = s.fullName,
                    email = s.email,
                    password = s.password,
                    repeatPassword = s.repeatPassword,
                    requestedRole = s.selectedRole,
                )
            ) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Заявка на регистрацию отправлена. Доступ появится после подтверждения администратором.",
                        )
                    }
                }
                is AppResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
}
