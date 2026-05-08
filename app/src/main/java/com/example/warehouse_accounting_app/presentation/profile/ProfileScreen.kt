package com.example.warehouse_accounting_app.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppButton
import com.example.warehouse_accounting_app.core.ui.components.AppCard
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.core.ui.components.ConfirmDialog
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.core.ui.components.RoleChip
import com.example.warehouse_accounting_app.core.ui.components.StatusChip
import com.example.warehouse_accounting_app.domain.model.User

@Composable
fun ProfileScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val viewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        ConfirmDialog(
            title = "Выход",
            text = "Вы уверены, что хотите выйти из приложения?",
            confirmText = "Выйти",
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout(onDone = onLogout)
            },
            onDismiss = { showLogoutDialog = false },
        )
    }

    AppScaffold(
        topBar = { AppTopBar(title = "Профиль", onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when {
                state.isLoading -> LoadingContent()
                state.errorMessage != null -> ErrorContent(state.errorMessage!!)
                state.user != null -> ProfileContent(
                    user = state.user!!,
                    onLogout = { showLogoutDialog = true },
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(user: User, onLogout: () -> Unit) {
    AppCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
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
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    AppButton(
        text = "Выйти из приложения",
        modifier = Modifier.fillMaxWidth(),
        onClick = onLogout,
    )
}
