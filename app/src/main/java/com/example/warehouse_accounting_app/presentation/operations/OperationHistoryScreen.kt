package com.example.warehouse_accounting_app.presentation.operations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.reports.OperationReport

@Composable
fun OperationHistoryScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
) {
    val viewModel: OperationHistoryViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "История операций",
                onBack = onBack,
                actions = { IconButton(onClick = viewModel::load) { Icon(Icons.Filled.Refresh, contentDescription = "Обновить", tint = Color.White) } },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.searchQuery, onValueChange = viewModel::onSearchChange,
                modifier = Modifier.fillMaxWidth(), placeholder = { Text("Поиск по товару или сотруднику…") },
                singleLine = true, shape = MaterialTheme.shapes.medium,
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(selected = state.typeFilter == null, onClick = { viewModel.onTypeFilter(null) }, label = { Text("Все") }) }
                StockOperationType.entries.forEach { type ->
                    item {
                        FilterChip(
                            selected = state.typeFilter == type,
                            onClick = { viewModel.onTypeFilter(if (state.typeFilter == type) null else type) },
                            label = { Text(type.label()) },
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            when {
                state.isLoading -> LoadingContent()
                state.errorMessage != null -> ErrorContent(state.errorMessage!!)
                state.filtered.isEmpty() -> EmptyContent("Операций не найдено")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.filtered, key = { it.operationId }) { OperationCard(it) }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun OperationCard(op: OperationReport) {
    val (icon, color) = op.operationType.iconAndColor()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(op.operationType.label(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = color)
                    Text(op.createdAt.take(16).replace("T", " "), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${op.productName} (${op.productArticle})", style = MaterialTheme.typography.bodyMedium)
                Text("Кол-во: ${op.quantity}${if (op.price != null) " · Цена: ${op.price}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Склад: ${op.warehouseName} · ${op.createdByName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun StockOperationType.label(): String = when (this) {
    StockOperationType.RECEIPT -> "Приход"
    StockOperationType.ISSUE -> "Расход"
    StockOperationType.WRITE_OFF -> "Списание"
    StockOperationType.INVENTORY -> "Инвентаризация"
}

private fun StockOperationType.iconAndColor(): Pair<ImageVector, Color> = when (this) {
    StockOperationType.RECEIPT -> Icons.Filled.MoveToInbox to Color(0xFF198754)
    StockOperationType.ISSUE -> Icons.Filled.Output to Color(0xFFE67E22)
    StockOperationType.WRITE_OFF -> Icons.Filled.DeleteForever to Color(0xFFDC3545)
    StockOperationType.INVENTORY -> Icons.Filled.FactCheck to Color(0xFF0D6EFD)
}
