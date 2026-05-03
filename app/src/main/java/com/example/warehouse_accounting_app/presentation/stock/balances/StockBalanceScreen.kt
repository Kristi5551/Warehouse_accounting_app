package com.example.warehouse_accounting_app.presentation.stock.balances

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
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockInStockContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockLow
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockLowContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockOutContainer
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
import com.example.warehouse_accounting_app.core.ui.components.StockStatusChip
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
                    IconButton(onClick = { viewModel.onEvent(StockBalanceEvent.Refresh) }) {
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
                onValueChange = { viewModel.onEvent(StockBalanceEvent.SearchChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск по названию или артикулу…") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.statusFilter == null,
                        onClick = { viewModel.onEvent(StockBalanceEvent.StatusFilterChanged(null)) },
                        label = { Text("Все") },
                    )
                }
                item {
                    FilterChip(
                        selected = state.statusFilter == StockStatus.IN_STOCK,
                        onClick = {
                            viewModel.onEvent(
                                StockBalanceEvent.StatusFilterChanged(
                                    if (state.statusFilter == StockStatus.IN_STOCK) null else StockStatus.IN_STOCK,
                                ),
                            )
                        },
                        label = { Text("В наличии") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColorStockInStockContainer,
                        ),
                    )
                }
                item {
                    FilterChip(
                        selected = state.statusFilter == StockStatus.LOW_STOCK,
                        onClick = {
                            viewModel.onEvent(
                                StockBalanceEvent.StatusFilterChanged(
                                    if (state.statusFilter == StockStatus.LOW_STOCK) null else StockStatus.LOW_STOCK,
                                ),
                            )
                        },
                        label = { Text("Низкий остаток") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColorStockLowContainer,
                        ),
                    )
                }
                item {
                    FilterChip(
                        selected = state.statusFilter == StockStatus.OUT_OF_STOCK,
                        onClick = {
                            viewModel.onEvent(
                                StockBalanceEvent.StatusFilterChanged(
                                    if (state.statusFilter == StockStatus.OUT_OF_STOCK) null else StockStatus.OUT_OF_STOCK,
                                ),
                            )
                        },
                        label = { Text("Нет в наличии") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ColorStockOutContainer,
                        ),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            when {
                state.isLoading && state.balances.isEmpty() -> LoadingContent()
                state.errorMessage != null && state.balances.isEmpty() ->
                    ErrorContent(state.errorMessage!!)
                state.balances.isEmpty() ->
                    EmptyContent("Остатки не найдены")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.balances, key = { it.id }) { BalanceCard(it) }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun LowStockScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
) {
    val viewModel: LowStockViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val list = state.filteredForLow

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Низкие остатки",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { viewModel.onEvent(StockBalanceEvent.Refresh) }) {
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
                onValueChange = { viewModel.onEvent(StockBalanceEvent.SearchChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск по названию или артикулу…") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Товары, у которых остаток не выше минимального порога",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            when {
                state.isLoading && state.balances.isEmpty() -> LoadingContent()
                state.errorMessage != null && state.balances.isEmpty() ->
                    ErrorContent(state.errorMessage!!)
                list.isEmpty() ->
                    EmptyContent("Остатки не найдены")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(list, key = { it.id }) { LowBalanceCard(it) }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun BalanceCard(balance: StockBalance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    balance.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                StockStatusChip(balance.status)
            }
            Text(
                "Артикул: ${balance.productArticle}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (!balance.categoryName.isNullOrBlank()) {
                Text(
                    balance.categoryName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                "Склад: ${balance.warehouseName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.Bottom) {
                Column {
                    Text("Количество", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        formatQty(balance.quantity),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column {
                    Text("Минимум", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        formatQty(balance.minStock),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun LowBalanceCard(balance: StockBalance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = ColorStockLowContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(balance.productName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                StockStatusChip(balance.status)
            }
            Text("Артикул: ${balance.productArticle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Категория: ${balance.categoryName ?: "—"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Text("Склад: ${balance.warehouseName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Остаток: ${formatQty(balance.quantity)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = ColorStockLow,
                )
                Text("Мин.: ${formatQty(balance.minStock)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun formatQty(v: Double): String {
    val s = if (v % 1.0 == 0.0) v.toInt().toString() else String.format("%.3f", v).trimEnd('0').trimEnd('.')
    return s
}
