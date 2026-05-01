package com.example.warehouse_accounting_app.presentation.reports

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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport

@Composable
fun ReportsScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
) {
    val viewModel: ReportsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Отчёты",
                onBack = onBack,
                actions = { IconButton(onClick = viewModel::load) { Icon(Icons.Filled.Refresh, contentDescription = "Обновить", tint = Color.White) } },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> LoadingContent()
            state.errorMessage != null && state.stockSummary.isEmpty() -> ErrorContent(state.errorMessage!!)
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item { Spacer(Modifier.height(4.dp)) }
                    item { SectionHeader("Стоимость складских запасов") }
                    if (state.stockValueReport.isEmpty()) {
                        item { EmptySection("Нет данных") }
                    } else {
                        items(state.stockValueReport) { ValueCard(it) }
                    }
                    item { SectionHeader("Низкие остатки") }
                    if (state.lowStockReport.isEmpty()) {
                        item { EmptySection("Товаров с низким остатком нет") }
                    } else {
                        items(state.lowStockReport.take(5)) { LowStockItem(it) }
                        if (state.lowStockReport.size > 5) {
                            item { Text("... и ещё ${state.lowStockReport.size - 5} товаров", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp)) }
                        }
                    }
                    item { SectionHeader("Сводка по остаткам (топ 10)") }
                    if (state.stockSummary.isEmpty()) {
                        item { EmptySection("Нет данных") }
                    } else {
                        items(state.stockSummary.take(10)) { SummaryItem(it) }
                    }
                    item { SectionHeader("Последние операции") }
                    if (state.operationsReport.isEmpty()) {
                        item { EmptySection("Операций не найдено") }
                    } else {
                        val ops = state.operationsReport.take(10)
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
                                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                    ops.forEachIndexed { i, op ->
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(op.productName, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                            Text(op.operationType.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                        if (i < ops.lastIndex) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
}

@Composable
private fun EmptySection(msg: String) {
    Text(msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
}

@Composable
private fun ValueCard(v: StockValueReport) {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(v.warehouseName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Закупочная стоимость", style = MaterialTheme.typography.bodySmall)
                Text("${"%,.2f".format(v.totalPurchaseValue)} ₽", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Продажная стоимость", style = MaterialTheme.typography.bodySmall)
                Text("${"%,.2f".format(v.totalSaleValue)} ₽", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun LowStockItem(item: LowStockReport) {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFE67E22))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.productName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text("Остаток: ${item.quantity}  Мин.: ${item.minStock}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SummaryItem(item: StockSummaryReport) {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.productName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(item.productArticle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("${item.quantity} ${item.unit}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
