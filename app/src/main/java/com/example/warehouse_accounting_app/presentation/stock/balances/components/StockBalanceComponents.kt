package com.example.warehouse_accounting_app.presentation.stock.balances.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.warehouse_accounting_app.core.ui.components.EmptyContent
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.core.ui.components.StockStatusChip
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockInStockContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockLow
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockLowContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockOutContainer
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockStatus
import com.example.warehouse_accounting_app.presentation.stock.balances.StockBalanceEvent
import com.example.warehouse_accounting_app.presentation.stock.balances.StockBalanceState
import com.example.warehouse_accounting_app.core.util.NumberFormatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StockBalanceSearchAndStatusFilters(
    state: StockBalanceState,
    onEvent: (StockBalanceEvent) -> Unit,
) {
    OutlinedTextField(
        value = state.searchQuery,
        onValueChange = { onEvent(StockBalanceEvent.SearchChanged(it)) },
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
                onClick = { onEvent(StockBalanceEvent.StatusFilterChanged(null)) },
                label = { Text("Все") },
            )
        }
        item {
            FilterChip(
                selected = state.statusFilter == StockStatus.IN_STOCK,
                onClick = {
                    onEvent(
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
                    onEvent(
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
                    onEvent(
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
}

@Composable
internal fun StockBalanceListSection(
    state: StockBalanceState,
    onEvent: (StockBalanceEvent) -> Unit,
) {
    when {
        state.isLoading && state.balances.isEmpty() -> LoadingContent()
        state.errorMessage != null && state.balances.isEmpty() ->
            ErrorContent(
                message = state.errorMessage!!,
                onRetry = { onEvent(StockBalanceEvent.Refresh) },
            )
        state.balances.isEmpty() ->
            EmptyContent("Остатки не найдены")
        else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.balances, key = { it.id }) { BalanceCard(it) }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LowStockSearchField(
    state: StockBalanceState,
    onEvent: (StockBalanceEvent) -> Unit,
) {
    OutlinedTextField(
        value = state.searchQuery,
        onValueChange = { onEvent(StockBalanceEvent.SearchChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Поиск по названию или артикулу…") },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
    )
}

@Composable
internal fun LowStockExplanationText() {
    Text(
        text = "Товары, у которых остаток не выше минимального порога",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
internal fun LowStockListSection(
    state: StockBalanceState,
    balancesToShow: List<StockBalance>,
    onEvent: (StockBalanceEvent) -> Unit,
) {
    when {
        state.isLoading && state.balances.isEmpty() -> LoadingContent()
        state.errorMessage != null && state.balances.isEmpty() ->
            ErrorContent(
                message = state.errorMessage!!,
                onRetry = { onEvent(StockBalanceEvent.Refresh) },
            )
        balancesToShow.isEmpty() ->
            EmptyContent("Остатки не найдены")
        else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(balancesToShow, key = { it.id }) { LowBalanceCard(it) }
            item { Spacer(Modifier.height(16.dp)) }
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                StockStatusChip(balance.status)
            }
            Text(
                "Артикул: ${balance.productArticle}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!balance.categoryName.isNullOrBlank()) {
                Text(
                    balance.categoryName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                "Склад: ${balance.warehouseName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.Bottom) {
                Column {
                    Text("Количество", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        NumberFormatters.quantityDisplay(balance.quantity),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column {
                    Text("Минимум", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        NumberFormatters.quantityDisplay(balance.minStock),
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
                Text(
                    balance.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                StockStatusChip(balance.status)
            }
            Text(
                "Артикул: ${balance.productArticle}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "Категория: ${balance.categoryName ?: "—"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "Склад: ${balance.warehouseName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Остаток: ${NumberFormatters.quantityDisplay(balance.quantity)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = ColorStockLow,
                )
                Text("Мин.: ${NumberFormatters.quantityDisplay(balance.minStock)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
