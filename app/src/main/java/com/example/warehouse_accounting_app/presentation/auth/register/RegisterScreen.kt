package com.example.warehouse_accounting_app.presentation.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppButton
import com.example.warehouse_accounting_app.core.ui.components.AppOutlinedButton
import com.example.warehouse_accounting_app.core.ui.components.AppPasswordTextField
import com.example.warehouse_accounting_app.core.ui.components.AppTextField
import com.example.warehouse_accounting_app.domain.model.UserRole

@Composable
fun RegisterScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBackToLogin: () -> Unit,
) {
    val viewModel: RegisterViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Регистрация",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Создайте учётную запись",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                AppTextField(
                    value = state.fullName,
                    onValueChange = { viewModel.onEvent(RegisterEvent.FullNameChanged(it)) },
                    label = "ФИО",
                    modifier = Modifier.fillMaxWidth(),
                )
                AppTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
                    label = "Email",
                    modifier = Modifier.fillMaxWidth(),
                )
                AppPasswordTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
                    label = "Пароль",
                    modifier = Modifier.fillMaxWidth(),
                )
                AppPasswordTextField(
                    value = state.repeatPassword,
                    onValueChange = { viewModel.onEvent(RegisterEvent.RepeatPasswordChanged(it)) },
                    label = "Повторите пароль",
                    modifier = Modifier.fillMaxWidth(),
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Роль",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        FilterChip(
                            selected = state.selectedRole == UserRole.STOREKEEPER,
                            onClick = { viewModel.onEvent(RegisterEvent.RoleChanged(UserRole.STOREKEEPER)) },
                            label = { Text("Кладовщик") },
                        )
                        FilterChip(
                            selected = state.selectedRole == UserRole.MANAGER,
                            onClick = { viewModel.onEvent(RegisterEvent.RoleChanged(UserRole.MANAGER)) },
                            label = { Text("Менеджер") },
                        )
                    }
                }
                state.errorMessage?.let { msg ->
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                state.successMessage?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    if (state.successMessage == null) {
                        AppButton(
                            text = "Зарегистрироваться",
                            modifier = Modifier.fillMaxWidth(),
                        ) { viewModel.onEvent(RegisterEvent.Submit) }
                    }
                    AppOutlinedButton(
                        text = "← Войти",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onBackToLogin,
                    )
                }
            }
        }
    }
}
