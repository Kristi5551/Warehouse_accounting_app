package com.example.warehouse_accounting_app.presentation.users.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.warehouse_accounting_app.core.ui.components.AppButton
import com.example.warehouse_accounting_app.core.ui.components.AppOutlinedButton
import com.example.warehouse_accounting_app.core.ui.components.AppPasswordTextField
import com.example.warehouse_accounting_app.core.ui.components.AppTextField
import com.example.warehouse_accounting_app.core.ui.components.EmptyContent
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.core.ui.components.RoleChip
import com.example.warehouse_accounting_app.core.ui.components.StatusChip
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.model.UserStatus
import com.example.warehouse_accounting_app.presentation.users.UserListEvent
import com.example.warehouse_accounting_app.presentation.users.UserListFilter
import com.example.warehouse_accounting_app.presentation.users.UserListState

@Composable
internal fun UserListFilterRow(
    state: UserListState,
    onEvent: (UserListEvent) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(UserListFilter.entries) { filter ->
            FilterChip(
                selected = state.filter == filter,
                onClick = { onEvent(UserListEvent.FilterChanged(filter)) },
                label = { Text(filter.label) },
            )
        }
    }
}

@Composable
internal fun UserListMainBody(
    state: UserListState,
    onEvent: (UserListEvent) -> Unit,
) {
    when {
        state.isLoading -> LoadingContent()
        state.errorMessage != null && state.users.isEmpty() ->
            ErrorContent(
                message = state.errorMessage!!,
                onRetry = { onEvent(UserListEvent.Refresh) },
            )
        state.users.isEmpty() -> EmptyContent("Пользователи не найдены")
        else ->
            Column {
                state.errorMessage?.let { msg ->
                    ErrorContent(
                        message = msg,
                        onRetry = { onEvent(UserListEvent.Refresh) },
                    )
                }
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.users, key = { it.id }) { user ->
                        UserCard(
                            user = user,
                            currentUserId = state.currentUserId,
                            enabled = !state.actionInProgress,
                            onApprove = { onEvent(UserListEvent.Approve(user.id)) },
                            onBlock = { onEvent(UserListEvent.Block(user.id)) },
                            onUnblock = { onEvent(UserListEvent.Unblock(user.id)) },
                            onChangeRole = { onEvent(UserListEvent.OpenRoleDialog(user.id)) },
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
    }
}

@Composable
internal fun UserRoleDialog(
    userId: Long,
    onEvent: (UserListEvent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onEvent(UserListEvent.CloseRoleDialog) },
        title = { Text("Изменить роль") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(
                    onClick = { onEvent(UserListEvent.ConfirmRole(userId, UserRole.ADMIN)) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Администратор") }
                TextButton(
                    onClick = { onEvent(UserListEvent.ConfirmRole(userId, UserRole.STOREKEEPER)) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Кладовщик") }
                TextButton(
                    onClick = { onEvent(UserListEvent.ConfirmRole(userId, UserRole.MANAGER)) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Менеджер") }
            }
        },
        confirmButton = {
            TextButton(onClick = { onEvent(UserListEvent.CloseRoleDialog) }) {
                Text("Закрыть")
            }
        },
    )
}

@Composable
internal fun UserCreateAdminDialog(
    state: UserListState,
    onEvent: (UserListEvent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            if (!state.actionInProgress) onEvent(UserListEvent.CloseCreateAdminDialog)
        },
        title = { Text("Новый администратор") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Будет создана активная учётная запись с полными правами администратора.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AppTextField(
                    value = state.newAdminFullName,
                    onValueChange = { onEvent(UserListEvent.CreateAdminFullNameChanged(it)) },
                    label = "ФИО",
                    modifier = Modifier.fillMaxWidth(),
                )
                AppTextField(
                    value = state.newAdminEmail,
                    onValueChange = { onEvent(UserListEvent.CreateAdminEmailChanged(it)) },
                    label = "Email",
                    modifier = Modifier.fillMaxWidth(),
                )
                AppPasswordTextField(
                    value = state.newAdminPassword,
                    onValueChange = { onEvent(UserListEvent.CreateAdminPasswordChanged(it)) },
                    label = "Пароль",
                    modifier = Modifier.fillMaxWidth(),
                )
                AppPasswordTextField(
                    value = state.newAdminRepeatPassword,
                    onValueChange = { onEvent(UserListEvent.CreateAdminRepeatPasswordChanged(it)) },
                    label = "Повторите пароль",
                    modifier = Modifier.fillMaxWidth(),
                )
                state.createAdminFormError?.let { err ->
                    Text(
                        text = err,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onEvent(UserListEvent.SubmitCreateAdmin) },
                enabled = !state.actionInProgress,
            ) { Text("Создать") }
        },
        dismissButton = {
            TextButton(
                onClick = { onEvent(UserListEvent.CloseCreateAdminDialog) },
                enabled = !state.actionInProgress,
            ) { Text("Отмена") }
        },
    )
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoleChip(user.role)
                StatusChip(user.status)
                if (isSelf) {
                    Text(
                        text = "Вы",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            when (user.status) {
                UserStatus.PENDING -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppButton("Подтвердить", enabled = enabled, onClick = onApprove)
                    if (!isSelf) AppOutlinedButton("Заблокировать", enabled = enabled, onClick = onBlock)
                }
                UserStatus.ACTIVE -> if (!isSelf) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppOutlinedButton("Заблокировать", enabled = enabled, onClick = onBlock)
                    AppOutlinedButton("Изменить роль", enabled = enabled, onClick = onChangeRole)
                }
                UserStatus.BLOCKED -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppButton("Разблокировать", enabled = enabled, onClick = onUnblock)
                    if (!isSelf) AppOutlinedButton("Изменить роль", enabled = enabled, onClick = onChangeRole)
                }
            }
        }
    }
}
