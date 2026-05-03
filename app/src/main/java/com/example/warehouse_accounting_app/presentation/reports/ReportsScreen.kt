package com.example.warehouse_accounting_app.presentation.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.core.ui.format.ruLabel
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationReportLine
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReportItem
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
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
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ReportsEvent.RefreshAll) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить", tint = Color.White)
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading && state.stockSummary == null && state.stockValueReport == null ->
                LoadingContent(Modifier.padding(padding))

            state.errorMessage != null && state.stockSummary == null ->
                Column(Modifier.fillMaxSize().padding(padding)) {
                    ErrorContent(
                        message = state.errorMessage!!,
                        onRetry = { viewModel.onEvent(ReportsEvent.RefreshAll) },
                    )
                }

            else ->
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item { Spacer(Modifier.height(4.dp)) }

                    state.errorMessage?.let { msg ->
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    msg,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        }
                    }

                    item {
                        SectionTitle("Сводка по складу")
                        val sum = state.stockSummary
                        if (sum == null) {
                            EmptyHint("Нет данных сводки")
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                StatCard(title = "Всего позиций", value = sum.totalProducts.toString(), accent = MaterialTheme.colorScheme.primary)
                                StatCard(title = "В наличии", value = sum.inStockCount.toString(), accent = Color(0xFF198754))
                                StatCard(title = "Низкий остаток", value = sum.lowStockCount.toString(), accent = Color(0xFFE67E22))
                                StatCard(title = "Нет в наличии", value = sum.outOfStockCount.toString(), accent = Color(0xFFDC3545))
                            }
                        }
                    }

                    item {
                        SectionTitle("Стоимость запасов")
                        val vr = state.stockValueReport
                        if (vr == null) {
                            EmptyHint("Нет данных по стоимости")
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                    ),
                                elevation = CardDefaults.cardElevation(2.dp),
                            ) {
                                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Общая стоимость", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Text(
                                        formatMoneyRub(vr.totalValue),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            if (vr.items.isEmpty()) {
                                EmptyHint("Нет позиций для оценки")
                            }
                        }
                    }

                    items(state.stockValueReport?.items.orEmpty()) { item ->
                        ValueItemRow(item)
                    }

                    item {
                        SectionTitle("Низкие остатки")
                        if (state.lowStockReport.isEmpty()) {
                            EmptyHint("Товаров с остатком ≤ минимума нет")
                        }
                    }

                    items(state.lowStockReport) { row ->
                        LowStockCard(row)
                    }

                    item {
                        SectionTitle("Движение товаров")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(1.dp),
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = state.dateFromInput,
                                        onValueChange = { viewModel.onEvent(ReportsEvent.DateFromChanged(it)) },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Дата от") },
                                        placeholder = { Text("ГГГГ-ММ-ДД") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                    )
                                    OutlinedTextField(
                                        value = state.dateToInput,
                                        onValueChange = { viewModel.onEvent(ReportsEvent.DateToChanged(it)) },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Дата до") },
                                        placeholder = { Text("ГГГГ-ММ-ДД") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                    )
                                }
                                Button(
                                    onClick = { viewModel.onEvent(ReportsEvent.ApplyOperationsPeriod) },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text("Обновить период")
                                }
                            }
                        }
                    }

                    item {
                        val op = state.operationsReport
                        if (op == null) {
                            EmptyHint("Загрузите отчёт по операциям")
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                StatCard(title = "Приходы", value = op.receiptCount.toString(), accent = Color(0xFF198754))
                                StatCard(title = "Расходы", value = op.issueCount.toString(), accent = Color(0xFFE67E22))
                                StatCard(title = "Списания", value = op.writeOffCount.toString(), accent = Color(0xFFDC3545))
                                StatCard(title = "Инвентаризации", value = op.inventoryCount.toString(), accent = Color(0xFF0D6EFD))
                            }
                        }
                    }

                    item {
                        val lines = state.operationsReport?.operations.orEmpty()
                        if (lines.isEmpty() && state.operationsReport != null) {
                            EmptyHint("За выбранный период операций нет")
                        }
                    }

                    items(
                        items = state.operationsReport?.operations?.take(25).orEmpty(),
                        key = { it.operationId },
                    ) { line ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(1.dp),
                        ) {
                            OperationRow(line, Modifier.padding(14.dp))
                        }
                    }

                    state.operationsReport?.operations?.let { all ->
                        if (all.size > 25) {
                            item {
                                Text(
                                    "Показаны первые 25 из ${all.size} строк",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.height(24.dp)) }
                }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}

@Composable
private fun StatCard(title: String, value: String, accent: Color) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
private fun ValueItemRow(item: StockValueReportItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(item.productName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text("Арт. ${item.productArticle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Кол-во: ${formatQty(item.quantity)}", style = MaterialTheme.typography.bodySmall)
                Text("Цена закуп.: ${formatMoneyRub(item.purchasePrice)}", style = MaterialTheme.typography.bodySmall)
            }
            Text("Стоимость: ${formatMoneyRub(item.value)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun LowStockCard(item: LowStockReport) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFE67E22), modifier = Modifier.size(28.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.productName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text("${item.warehouseName} · остаток ${formatQty(item.quantity)} · мин. ${formatQty(item.minStock)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun OperationRow(line: OperationReportLine, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(line.operationType.ruLabel(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Text(line.createdAt.take(16).replace("T", " "), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (line.items.size <= 1) {
            Text("${line.productName} (${line.productArticle})", style = MaterialTheme.typography.bodySmall)
            Text("Кол-во: ${formatQty(line.quantity)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            line.items.forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("${item.productName} (${item.productArticle})", style = MaterialTheme.typography.bodySmall)
                    val qtyPrice =
                        buildString {
                            append("Кол-во: ${formatQty(item.quantity)}")
                            item.price?.let { append(" · Цена: ${formatMoneyRub(it)}") }
                        }
                    Text(qtyPrice, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Text("${line.warehouseName} · ${line.createdByName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
}

private fun formatMoneyRub(v: Double): String =
    String.format(Locale.US, "%,.2f", v).replace(',', ' ') + " ₽"

private fun formatQty(q: Double): String {
    val s = String.format(Locale.US, "%.3f", q).trimEnd('0').trimEnd('.')
    return s.ifEmpty { "0" }
}
