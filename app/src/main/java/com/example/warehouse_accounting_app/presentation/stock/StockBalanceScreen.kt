package com.example.warehouse_accounting_app.presentation.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.core.ui.components.EmptyContent
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockBalanceScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
) {
    val viewModel: StockBalanceViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Остатки",
                onBack = onBack,
                actions = {
                    IconButton(onClick = viewModel::load) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить", tint = Color.White)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск по товару или артикулу…") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = state.statusFilter == null, onClick = { viewModel.onStatusFilter(null) }, label = { Text("Все") })
                }
                item {
                    FilterChip(
                        selected = state.statusFilter == StockStatus.IN_STOCK,
                        onClick = { viewModel.onStatusFilter(if (state.statusFilter == StockStatus.IN_STOCK) null else StockStatus.IN_STOCK) },
                        label = { Text("В наличии") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFD4EDDA)),
                    )
                }
                item {
                    FilterChip(
                        selected = state.statusFilter == StockStatus.LOW_STOCK,
                        onClick = { viewModel.onStatusFilter(if (state.statusFilter == StockStatus.LOW_STOCK) null else StockStatus.LOW_STOCK) },
                        label = { Text("Низкий остаток") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFF3CD)),
                    )
                }
                item {
                    FilterChip(
                        selected = state.statusFilter == StockStatus.OUT_OF_STOCK,
                        onClick = { viewModel.onStatusFilter(if (state.statusFilter == StockStatus.OUT_OF_STOCK) null else StockStatus.OUT_OF_STOCK) },
                        label = { Text("Нет в наличии") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFF8D7DA)),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            when {
                state.isLoading -> LoadingContent()
                state.errorMessage != null -> ErrorContent(state.errorMessage!!)
                state.filtered.isEmpty() -> EmptyContent(if (state.searchQuery.isBlank() && state.statusFilter == null) "Остатки не найдены" else "Ничего не найдено")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.filtered, key = { it.id }) { BalanceCard(it) }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(balance: StockBalance) {
    val statusColor = when (balance.status) {
        StockStatus.IN_STOCK -> Color(0xFF198754)
        StockStatus.LOW_STOCK -> Color(0xFFE67E22)
        StockStatus.OUT_OF_STOCK -> MaterialTheme.colorScheme.error
    }
    val statusLabel = when (balance.status) {
        StockStatus.IN_STOCK -> "В наличии"
        StockStatus.LOW_STOCK -> "Низкий остаток"
        StockStatus.OUT_OF_STOCK -> "Нет в наличии"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(balance.productName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(statusLabel, style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Medium)
            }
            Text("Арт: ${balance.productArticle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!balance.categoryName.isNullOrBlank()) Text(balance.categoryName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text("Склад: ${balance.warehouseName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Кол-во: ${balance.quantity}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text("Мин.: ${balance.minStock}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
