package com.example.warehouse_accounting_app.presentation.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppButton
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.model.UserStatus

@Composable
fun UserListScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
    onSessionExpired: () -> Unit,
) {
    val viewModel: UserListViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.sessionExpired) {
        if (state.sessionExpired) {
            onSessionExpired()
            viewModel.onEvent(UserListEvent.SessionExpiredConsumed)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AppButton(text = "Назад", onClick = onBack)
            AppButton(text = "Обновить", enabled = !state.isLoading && !state.actionInProgress) {
                viewModel.onEvent(UserListEvent.Refresh)
            }
        }
        Text("Пользователи", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.padding(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppButton(
                text = "Все",
                enabled = state.filter != UserListFilter.ALL,
            ) { viewModel.onEvent(UserListEvent.ShowAll) }
            AppButton(
                text = "Ожидают подтверждения",
                enabled = state.filter != UserListFilter.PENDING,
            ) { viewModel.onEvent(UserListEvent.ShowPending) }
        }
        state.errorMessage?.let { msg ->
            Spacer(Modifier.padding(4.dp))
            ErrorContent(msg)
            Spacer(Modifier.padding(4.dp))
            AppButton("Скрыть ошибку") { viewModel.onEvent(UserListEvent.ClearMessages) }
        }
        state.successMessage?.let { msg ->
            Text(msg, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.padding(4.dp))
            AppButton("Ок") { viewModel.onEvent(UserListEvent.ClearMessages) }
        }
        when {
            state.isLoading -> LoadingContent()
            else -> LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.users, key = { it.id }) { user ->
                    UserCard(
                        user = user,
                        currentUserId = state.currentUserId,
                        enabled = !state.actionInProgress,
                        onApprove = { viewModel.onEvent(UserListEvent.Approve(user.id)) },
                        onBlock = { viewModel.onEvent(UserListEvent.Block(user.id)) },
                        onUnblock = { viewModel.onEvent(UserListEvent.Unblock(user.id)) },
                        onChangeRole = { viewModel.onEvent(UserListEvent.OpenRoleDialog(user.id)) },
                    )
                }
            }
        }
    }

    state.roleDialogUserId?.let { userId ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(UserListEvent.CloseRoleDialog) },
            title = { Text("Выбор роли") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = {
                            viewModel.onEvent(UserListEvent.ConfirmRole(userId, UserRole.ADMIN))
                        },
                    ) { Text("Администратор") }
                    TextButton(
                        onClick = {
                            viewModel.onEvent(UserListEvent.ConfirmRole(userId, UserRole.STOREKEEPER))
                        },
                    ) { Text("Кладовщик") }
                    TextButton(
                        onClick = {
                            viewModel.onEvent(UserListEvent.ConfirmRole(userId, UserRole.MANAGER))
                        },
                    ) { Text("Менеджер") }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(UserListEvent.CloseRoleDialog) }) {
                    Text("Закрыть")
                }
            },
        )
    }
}

@Composable
private fun UserCard(
    user: User,
    currentUserId: Long?,
    enabled: Boolean,
    onApprove: () -> Unit,
    onBlock: () -> Unit,
    onUnblock: () -> Unit,
    onChangeRole: () -> Unit,
) {
    val isSelf = currentUserId != null && user.id == currentUserId
    Card(colors = CardDefaults.cardColors()) {
        Column(Modifier.padding(12.dp)) {
            Text(user.fullName, style = MaterialTheme.typography.titleMedium)
            Text(user.email, style = MaterialTheme.typography.bodyMedium)
            Text("Роль: ${user.role.localized()}", style = MaterialTheme.typography.bodySmall)
            Text("Статус: ${user.status.localized()}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.padding(4.dp))
            when (user.status) {
                UserStatus.PENDING -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppButton("Подтвердить", enabled = enabled, onClick = onApprove)
                        if (!isSelf) {
                            AppButton("Заблокировать", enabled = enabled, onClick = onBlock)
                        }
                    }
                }
                UserStatus.ACTIVE -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (!isSelf) {
                            AppButton("Заблокировать", enabled = enabled, onClick = onBlock)
                        }
                        if (!isSelf) {
                            AppButton("Изменить роль", enabled = enabled, onClick = onChangeRole)
                        }
                    }
                }
                UserStatus.BLOCKED -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppButton("Разблокировать", enabled = enabled, onClick = onUnblock)
                        if (!isSelf) {
                            AppButton("Изменить роль", enabled = enabled, onClick = onChangeRole)
                        }
                    }
                }
            }
        }
    }
}

private fun UserRole.localized(): String =
    when (this) {
        UserRole.ADMIN -> "Администратор"
        UserRole.STOREKEEPER -> "Кладовщик"
        UserRole.MANAGER -> "Менеджер"
    }

private fun UserStatus.localized(): String =
    when (this) {
        UserStatus.PENDING -> "Ожидает подтверждения"
        UserStatus.ACTIVE -> "Активен"
        UserStatus.BLOCKED -> "Заблокирован"
    }
