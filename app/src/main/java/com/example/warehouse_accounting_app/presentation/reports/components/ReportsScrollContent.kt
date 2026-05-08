package com.example.warehouse_accounting_app.presentation.reports.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.warehouse_accounting_app.core.ui.format.ruLabel
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationReportLine
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReportItem
import com.example.warehouse_accounting_app.presentation.reports.ReportsEvent
import com.example.warehouse_accounting_app.presentation.reports.ReportsState
import com.example.warehouse_accounting_app.core.util.NumberFormatters
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
internal fun LazyListScope.reportsScrollItems(
    state: ReportsState,
    onEvent: (ReportsEvent) -> Unit,
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
        ReportsSectionTitle("Сводка по складу")
        val sum = state.stockSummary
        if (sum == null) {
            ReportsEmptyHint("Нет данных сводки")
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ReportsStatCard(title = "Всего позиций", value = sum.totalProducts.toString(), accent = MaterialTheme.colorScheme.primary)
                ReportsStatCard(title = "В наличии", value = sum.inStockCount.toString(), accent = Color(0xFF198754))
                ReportsStatCard(title = "Низкий остаток", value = sum.lowStockCount.toString(), accent = Color(0xFFE67E22))
                ReportsStatCard(title = "Нет в наличии", value = sum.outOfStockCount.toString(), accent = Color(0xFFDC3545))
            }
        }
    }

    item {
        ReportsSectionTitle("Стоимость запасов")
        val vr = state.stockValueReport
        if (vr == null) {
            ReportsEmptyHint("Нет данных по стоимости")
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
                        reportsFormatMoneyRub(vr.totalValue),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            if (vr.items.isEmpty()) {
                ReportsEmptyHint("Нет позиций для оценки")
            }
        }
    }

    items(state.stockValueReport?.items.orEmpty()) { item ->
        ReportsValueItemRow(item)
    }

    item {
        ReportsSectionTitle("Низкие остатки")
        if (state.lowStockReport.isEmpty()) {
            ReportsEmptyHint("Товаров с остатком ≤ минимума нет")
        }
    }

    items(state.lowStockReport) { row ->
        ReportsLowStockCard(row)
    }

    item {
        ReportsSectionTitle("Движение товаров")
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
                        onValueChange = { onEvent(ReportsEvent.DateFromChanged(it)) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Дата от") },
                        placeholder = { Text("ГГГГ-ММ-ДД") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                    )
                    OutlinedTextField(
                        value = state.dateToInput,
                        onValueChange = { onEvent(ReportsEvent.DateToChanged(it)) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Дата до") },
                        placeholder = { Text("ГГГГ-ММ-ДД") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                    )
                }
                Button(
                    onClick = { onEvent(ReportsEvent.ApplyOperationsPeriod) },
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
            ReportsEmptyHint("Загрузите отчёт по операциям")
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ReportsStatCard(title = "Приходы", value = op.receiptCount.toString(), accent = Color(0xFF198754))
                ReportsStatCard(title = "Расходы", value = op.issueCount.toString(), accent = Color(0xFFE67E22))
                ReportsStatCard(title = "Списания", value = op.writeOffCount.toString(), accent = Color(0xFFDC3545))
                ReportsStatCard(title = "Инвентаризации", value = op.inventoryCount.toString(), accent = Color(0xFF0D6EFD))
            }
        }
    }

    item {
        val lines = state.operationsReport?.operations.orEmpty()
        if (lines.isEmpty() && state.operationsReport != null) {
            ReportsEmptyHint("За выбранный период операций нет")
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
            ReportsOperationRow(line, Modifier.padding(14.dp))
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

@Composable
internal fun ReportsSectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}

@Composable
internal fun ReportsStatCard(
    title: String,
    value: String,
    accent: Color,
) {
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
internal fun ReportsValueItemRow(item: StockValueReportItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(item.productName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text("Арт. ${item.productArticle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Кол-во: ${reportsFormatQty(item.quantity)}", style = MaterialTheme.typography.bodySmall)
                Text("Цена закуп.: ${reportsFormatMoneyRub(item.purchasePrice)}", style = MaterialTheme.typography.bodySmall)
            }
            Text("Стоимость: ${reportsFormatMoneyRub(item.value)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
internal fun ReportsLowStockCard(item: LowStockReport) {
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
                Text("${item.warehouseName} · остаток ${reportsFormatQty(item.quantity)} · мин. ${reportsFormatQty(item.minStock)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
internal fun ReportsOperationRow(
    line: OperationReportLine,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(line.operationType.ruLabel(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Text(line.createdAt.take(16).replace("T", " "), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (line.items.size <= 1) {
            Text("${line.productName} (${line.productArticle})", style = MaterialTheme.typography.bodySmall)
            Text("Кол-во: ${reportsFormatQty(line.quantity)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            line.items.forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("${item.productName} (${item.productArticle})", style = MaterialTheme.typography.bodySmall)
                    val qtyPrice =
                        buildString {
                            append("Кол-во: ${reportsFormatQty(item.quantity)}")
                            item.price?.let { append(" · Цена: ${reportsFormatMoneyRub(it)}") }
                        }
                    Text(qtyPrice, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Text("${line.warehouseName} · ${line.createdByName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun ReportsEmptyHint(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
}

internal fun reportsFormatMoneyRub(v: Double): String =
    String.format(Locale.US, "%,.2f", v).replace(',', ' ') + " ₽"

internal fun reportsFormatQty(q: Double): String = NumberFormatters.quantityDisplay(q)
