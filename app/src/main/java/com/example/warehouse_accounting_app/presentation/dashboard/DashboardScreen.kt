package com.example.warehouse_accounting_app.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.navigation.AppRoutes
import com.example.warehouse_accounting_app.core.ui.components.AppButton
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent

@Composable
fun DashboardScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
) {
    val viewModel: DashboardViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Складской учет", style = MaterialTheme.typography.headlineSmall)
        when {
            state.isLoading -> LoadingContent()
            state.errorMessage != null -> ErrorContent(state.errorMessage!!)
            state.user != null -> {
                val u = state.user!!
                Text("ФИО: ${u.fullName}", style = MaterialTheme.typography.bodyLarge)
                Text("Email: ${u.email}", style = MaterialTheme.typography.bodyLarge)
                Text("Роль: ${u.role.name}", style = MaterialTheme.typography.bodyLarge)
                Text("Статус: ${u.status.name}", style = MaterialTheme.typography.bodyLarge)
            }
        }
        Spacer(Modifier.height(8.dp))
        state.user?.let { user -> routeButtons(onNavigate, user) }
        Spacer(Modifier.height(16.dp))
        AppButton("Выйти") { viewModel.onLogout(onDone = onLogout) }
    }
}

@Composable
private fun routeButtons(onNavigate: (String) -> Unit, currentUser: User) {
    val routes = buildList {
        if (currentUser.role == UserRole.ADMIN) {
            add(AppRoutes.Users to "Пользователи")
        }
        add(AppRoutes.Categories to "Категории")
        add(AppRoutes.Products to "Товары")
        add(AppRoutes.ProductDetails to "Карточка товара (заглушка)")
        add(AppRoutes.ProductEdit to "Редактирование товара")
        add(AppRoutes.StockBalances to "Остатки")
        add(AppRoutes.Receipt to "Приход")
        add(AppRoutes.Issue to "Расход")
        add(AppRoutes.WriteOff to "Списание")
        add(AppRoutes.Inventory to "Инвентаризация")
        add(AppRoutes.OperationHistory to "История операций")
        add(AppRoutes.Reports to "Отчеты")
        add(AppRoutes.Profile to "Профиль")
    }
    routes.forEach { (route, label) ->
        AppButton(label) { onNavigate(route) }
    }
}
