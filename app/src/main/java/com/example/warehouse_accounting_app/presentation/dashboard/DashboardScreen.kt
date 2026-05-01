package com.example.warehouse_accounting_app.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.navigation.AppRoutes
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.core.ui.components.ConfirmDialog
import com.example.warehouse_accounting_app.core.ui.components.DashboardCard
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.core.ui.components.RoleChip
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole

private data class DashboardSection(
    val route: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
)

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

    if (showLogoutDialog) {
        ConfirmDialog(
            title = "Выход",
            text = "Вы уверены, что хотите выйти из приложения?",
            confirmText = "Выйти",
            onConfirm = {
                showLogoutDialog = false
                viewModel.onLogout(onDone = onLogout)
            },
            onDismiss = { showLogoutDialog = false },
        )
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Складской учёт",
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Выйти",
                            tint = Color.White,
                        )
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> LoadingContent()
            state.errorMessage != null -> ErrorContent(state.errorMessage!!)
            state.user != null -> {
                val sections = buildSections(state.user!!.role)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        WelcomeHeader(state.user!!)
                        Spacer(Modifier.height(8.dp))
                    }
                    items(sections) { section ->
                        DashboardCard(
                            title = section.title,
                            description = section.description,
                            icon = section.icon,
                            onClick = { onNavigate(section.route) },
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun WelcomeHeader(user: User) {
    Row(
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
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        RoleChip(user.role)
    }
}

private fun buildSections(role: UserRole): List<DashboardSection> = buildList {
    if (role == UserRole.ADMIN) {
        add(DashboardSection(AppRoutes.Users, "Пользователи", "Подтверждение и роли сотрудников", Icons.Filled.Group))
    }
    add(DashboardSection(AppRoutes.Categories, "Категории", "Группы товаров", Icons.Filled.Category))
    add(DashboardSection(AppRoutes.Products, "Товары", "Каталог номенклатуры", Icons.Filled.Inventory))
    add(DashboardSection(AppRoutes.StockBalances, "Остатки", "Текущее количество товаров", Icons.Filled.Warehouse))
    add(DashboardSection(AppRoutes.Receipt, "Приход", "Поступление товаров на склад", Icons.Filled.MoveToInbox))
    add(DashboardSection(AppRoutes.Issue, "Расход", "Выдача или продажа товаров", Icons.Filled.Output))
    add(DashboardSection(AppRoutes.WriteOff, "Списание", "Брак, потери и повреждения", Icons.Filled.DeleteForever))
    add(DashboardSection(AppRoutes.Inventory, "Инвентаризация", "Сверка учётных и фактических остатков", Icons.Filled.FactCheck))
    add(DashboardSection(AppRoutes.OperationHistory, "История", "Все движения товаров", Icons.Filled.History))
    add(DashboardSection(AppRoutes.Reports, "Отчёты", "Аналитика по складу", Icons.Filled.BarChart))
    add(DashboardSection(AppRoutes.Profile, "Профиль", "Данные учётной записи", Icons.Filled.Person))
}
