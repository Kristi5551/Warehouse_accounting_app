package com.example.warehouse_accounting_app.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.warehouse_accounting_app.presentation.auth.login.LoginViewModel
import com.example.warehouse_accounting_app.presentation.auth.register.RegisterViewModel
import com.example.warehouse_accounting_app.presentation.dashboard.DashboardViewModel
import com.example.warehouse_accounting_app.presentation.splash.SplashViewModel

class WarehouseViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val c = container
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(c.checkAuthStateUseCase) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(c.loginUseCase) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(c.registerUseCase) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(c.logoutUseCase, c.getCurrentUserUseCase) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
