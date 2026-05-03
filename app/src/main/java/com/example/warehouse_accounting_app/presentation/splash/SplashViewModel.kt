package com.example.warehouse_accounting_app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.usecase.auth.AuthCheckResult
import com.example.warehouse_accounting_app.domain.usecase.auth.CheckAuthStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SplashDestination {
    data object Login : SplashDestination()
    data object Dashboard : SplashDestination()
    /** Сервер недоступен или произошла ошибка — пользователя на Dashboard не пускаем. */
    data class Error(val message: String) : SplashDestination()
}

class SplashViewModel(
    private val checkAuthState: CheckAuthStateUseCase,
) : ViewModel() {
    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    fun startCheck() {
        _destination.value = null
        viewModelScope.launch {
            _destination.value = when (val result = checkAuthState()) {
                is AuthCheckResult.Authenticated -> SplashDestination.Dashboard
                is AuthCheckResult.Unauthenticated -> SplashDestination.Login
                is AuthCheckResult.NetworkError -> SplashDestination.Error(result.message)
                is AuthCheckResult.UnknownError -> SplashDestination.Error(result.message)
            }
        }
    }
}
