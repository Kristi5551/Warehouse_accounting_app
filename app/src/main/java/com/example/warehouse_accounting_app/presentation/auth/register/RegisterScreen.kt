package com.example.warehouse_accounting_app.presentation.auth.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.warehouse_accounting_app.domain.model.UserRole

@Composable
fun RegisterScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBackToLogin: () -> Unit,
) {
    val viewModel: RegisterViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text("Регистрация", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        AppTextField(
            value = state.fullName,
            onValueChange = { viewModel.onEvent(RegisterEvent.FullNameChanged(it)) },
            label = "ФИО",
        )
        Spacer(Modifier.height(8.dp))
        AppTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
            label = "Email",
        )
        Spacer(Modifier.height(8.dp))
        AppTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
            label = "Пароль",
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(Modifier.height(8.dp))
        AppTextField(
            value = state.repeatPassword,
            onValueChange = { viewModel.onEvent(RegisterEvent.RepeatPasswordChanged(it)) },
            label = "Повтор пароля",
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(Modifier.height(8.dp))
        Text("Роль: ${roleLabel(state.selectedRole)}", style = MaterialTheme.typography.bodyMedium)
        Row(Modifier.padding(vertical = 4.dp)) {
            AppButton(text = "Кладовщик") {
                viewModel.onEvent(RegisterEvent.RoleChanged(UserRole.STOREKEEPER))
            }
            Spacer(Modifier.width(8.dp))
            AppButton(text = "Менеджер") {
                viewModel.onEvent(RegisterEvent.RoleChanged(UserRole.MANAGER))
            }
        }
        state.errorMessage?.let { msg ->
            Spacer(Modifier.height(8.dp))
            ErrorContent(msg)
        }
        state.successMessage?.let { msg ->
            Spacer(Modifier.height(8.dp))
            Text(msg, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))
        if (state.isLoading) {
            LoadingContent()
        } else {
            AppButton("Зарегистрироваться") { viewModel.onEvent(RegisterEvent.Submit) }
            Spacer(Modifier.height(8.dp))
            AppButton("Уже есть аккаунт? Войти", onClick = onBackToLogin)
            if (state.successMessage != null) {
                Spacer(Modifier.height(8.dp))
                AppButton("На экран входа", onClick = onBackToLogin)
            }
        }
    }
}

private fun roleLabel(role: UserRole): String =
    when (role) {
        UserRole.STOREKEEPER -> "Кладовщик"
        UserRole.MANAGER -> "Менеджер"
        UserRole.ADMIN -> "Администратор"
    }
