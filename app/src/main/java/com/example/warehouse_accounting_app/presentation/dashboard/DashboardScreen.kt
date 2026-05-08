package com.example.warehouse_accounting_app.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.core.ui.components.ConfirmDialog
import com.example.warehouse_accounting_app.core.ui.components.DashboardCard
import com.example.warehouse_accounting_app.core.ui.components.EmptyContent
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.core.ui.components.RoleChip
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
) {
    val viewModel: DashboardViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.sessionExpired) {
        if (state.sessionExpired) onLogout()
    }

    if (showLogoutDialog) {
        ConfirmDialog(
            title = "Выход",
            text = "Вы уверены, что хотите выйти из аккаунта?",
            confirmText = "Выйти",
            onConfirm = { showLogoutDialog = false; viewModel.onLogout(onDone = onLogout) },
            onDismiss = { showLogoutDialog = false },
        )
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Складской учёт",
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Выйти", tint = Color.White)
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> LoadingContent()
            state.errorMessage != null -> ErrorContent(
                message = state.errorMessage!!,
                modifier = Modifier.padding(padding),
                onRetry = { viewModel.refresh() },
            )
            state.user != null -> {
                val sections = buildDashboardSections(state.user!!.role)
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        WelcomeHeader(state.user!!)
                        Spacer(Modifier.height(8.dp))
                        RoleSubtitle(state.user!!.role)
                        Spacer(Modifier.height(4.dp))
                    }
                    items(sections) { section ->
                        DashboardCard(
                            title = section.title,
                            description = section.description,
                            icon = section.icon,
                            onClick = { onNavigate(section.route) },
                        )
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
            else -> EmptyContent(
                message = "Для вашей роли пока нет доступных разделов",
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun WelcomeHeader(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Здравствуйте,",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        RoleChip(user.role)
    }
}

@Composable
private fun RoleSubtitle(role: UserRole) {
    val subtitle = when (role) {
        UserRole.ADMIN -> "Панель администратора"
        UserRole.STOREKEEPER -> "Панель кладовщика"
        UserRole.MANAGER -> "Панель менеджера"
    }
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
    )
}
