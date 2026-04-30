package com.example.warehouse_accounting_app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.domain.usecase.auth.CheckAuthStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SplashDestination {
    data object Login : SplashDestination()
    data object Dashboard : SplashDestination()
}

class SplashViewModel(
    private val checkAuthState: CheckAuthStateUseCase,
) : ViewModel() {
    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    fun startCheck() {
        viewModelScope.launch {
            val ok = checkAuthState()
            _destination.value = if (ok) SplashDestination.Dashboard else SplashDestination.Login
        }
    }
}
