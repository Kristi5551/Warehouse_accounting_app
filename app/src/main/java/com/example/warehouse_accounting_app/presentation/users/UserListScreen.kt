package com.example.warehouse_accounting_app.presentation.users

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppSnackbarHost
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.presentation.users.components.UserCreateAdminDialog
import com.example.warehouse_accounting_app.presentation.users.components.UserListFilterRow
import com.example.warehouse_accounting_app.presentation.users.components.UserListMainBody
import com.example.warehouse_accounting_app.presentation.users.components.UserRoleDialog

@Composable
fun UserListScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
    onSessionExpired: () -> Unit,
) {
    val viewModel: UserListViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.sessionExpired) {
        if (state.sessionExpired) {
            onSessionExpired()
            viewModel.onEvent(UserListEvent.SessionExpiredConsumed)
        }
    }
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.onEvent(UserListEvent.ClearMessages)
        }
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Пользователи",
                onBack = onBack,
                actions = {
                    IconButton(
                        onClick = { viewModel.onEvent(UserListEvent.OpenCreateAdminDialog) },
                        enabled = !state.isLoading && !state.actionInProgress,
                    ) {
                        Icon(Icons.Filled.PersonAdd, contentDescription = "Добавить администратора", tint = Color.White)
                    }
                    IconButton(
                        onClick = { viewModel.onEvent(UserListEvent.Refresh) },
                        enabled = !state.isLoading && !state.actionInProgress,
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить", tint = Color.White)
                    }
                },
            )
        },
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            UserListFilterRow(state = state, onEvent = viewModel::onEvent)
            UserListMainBody(state = state, onEvent = viewModel::onEvent)
        }
    }

    state.roleDialogUserId?.let { userId ->
        UserRoleDialog(userId = userId, onEvent = viewModel::onEvent)
    }

    if (state.createAdminDialogVisible) {
        UserCreateAdminDialog(state = state, onEvent = viewModel::onEvent)
    }
}
