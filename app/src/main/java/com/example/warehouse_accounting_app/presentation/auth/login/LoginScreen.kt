package com.example.warehouse_accounting_app.presentation.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppButton
import com.example.warehouse_accounting_app.core.ui.components.AppTextField
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent

@Composable
fun LoginScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onLoggedIn: () -> Unit,
    onRegister: () -> Unit,
) {
    val viewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Вход", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        AppTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
            label = "Email",
        )
        Spacer(Modifier.height(8.dp))
        AppTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
            label = "Пароль",
            visualTransformation = PasswordVisualTransformation(),
        )
        state.errorMessage?.let { msg ->
            Spacer(Modifier.height(8.dp))
            ErrorContent(msg)
        }
        Spacer(Modifier.height(16.dp))
        if (state.isLoading) {
            LoadingContent()
        } else {
            AppButton(
                text = "Войти",
                enabled = !state.isLoading,
            ) {
                viewModel.onEvent(LoginEvent.Submit, onSuccess = onLoggedIn)
            }
            Spacer(Modifier.height(8.dp))
            AppButton(text = "Зарегистрироваться") {
                viewModel.onEvent(LoginEvent.RegisterNavigation, onRegister = onRegister)
            }
        }
    }
}
