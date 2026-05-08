package com.example.warehouse_accounting_app.presentation.operations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.core.ui.components.EmptyContent
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.core.ui.format.ruLabel
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationItem
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.UserPick
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationHistoryScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
) {
    val viewModel: OperationHistoryViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    var productMenuExpanded by remember { mutableStateOf(false) }
    var userMenuExpanded by remember { mutableStateOf(false) }

    val selectedProduct: Product? = state.products.find { it.id == state.selectedProductId }
    val productFieldText =
        selectedProduct?.let { "${it.name} (арт. ${it.article})" } ?: "Все товары"
    val selectedUser: UserPick? = state.filterUsers.find { it.id == state.selectedUserId }
    val userFieldText = selectedUser?.fullName ?: "Все сотрудники"

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "История операций",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { viewModel.onEvent(OperationHistoryEvent.Refresh) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить", tint = Color.White)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp),
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Фильтры", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = state.typeFilter == null,
                                onClick = { viewModel.onEvent(OperationHistoryEvent.TypeFilterChanged(null)) },
                                label = { Text("Все") },
                            )
                        }
                        items(StockOperationType.entries.toList()) { type ->
                            FilterChip(
                                selected = state.typeFilter == type,
                                onClick = {
                                    viewModel.onEvent(
                                        OperationHistoryEvent.TypeFilterChanged(
                                            if (state.typeFilter == type) null else type,
                                        ),
                                    )
                                },
                                label = { Text(type.ruLabel()) },
                            )
                        }
                    }
                    Text(
                        "Тип операции применяется сразу. Остальные параметры — по кнопке «Применить».",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    ExposedDropdownMenuBox(
                        expanded = productMenuExpanded,
                        onExpandedChange = { productMenuExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = productFieldText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Товар") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productMenuExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = MaterialTheme.shapes.medium,
                        )
                        ExposedDropdownMenu(
                            expanded = productMenuExpanded,
                            onDismissRequest = { productMenuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Все товары") },
                                onClick = {
                                    viewModel.onEvent(OperationHistoryEvent.ProductFilterChanged(null))
                                    productMenuExpanded = false
                                },
                            )
                            state.products.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text("${p.name} (арт. ${p.article})") },
                                    onClick = {
                                        viewModel.onEvent(OperationHistoryEvent.ProductFilterChanged(p.id))
                                        productMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = userMenuExpanded,
                        onExpandedChange = { userMenuExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = userFieldText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Сотрудник") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = userMenuExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = MaterialTheme.shapes.medium,
                        )
                        ExposedDropdownMenu(
                            expanded = userMenuExpanded,
                            onDismissRequest = { userMenuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Все сотрудники") },
                                onClick = {
                                    viewModel.onEvent(OperationHistoryEvent.UserFilterChanged(null))
                                    userMenuExpanded = false
                                },
                            )
                            state.filterUsers.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text(u.fullName) },
                                    onClick = {
                                        viewModel.onEvent(OperationHistoryEvent.UserFilterChanged(u.id))
                                        userMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = state.dateFromInput,
                            onValueChange = { viewModel.onEvent(OperationHistoryEvent.DateFromChanged(it)) },
                            modifier = Modifier.weight(1f),
                            label = { Text("С даты") },
                            placeholder = { Text("ГГГГ-ММ-ДД") },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                        )
                        OutlinedTextField(
                            value = state.dateToInput,
                            onValueChange = { viewModel.onEvent(OperationHistoryEvent.DateToChanged(it)) },
                            modifier = Modifier.weight(1f),
                            label = { Text("По дату") },
                            placeholder = { Text("ГГГГ-ММ-ДД") },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                        )
                    }
                    Button(
                        onClick = { viewModel.onEvent(OperationHistoryEvent.ApplyFilters) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Применить")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            when {
                state.isLoading ->
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        LoadingContent(Modifier.fillMaxSize())
                    }

                state.errorMessage != null ->
                    Column(Modifier.weight(1f).fillMaxWidth()) {
                        ErrorContent(
                            message = state.errorMessage!!,
                            onRetry = { viewModel.onEvent(OperationHistoryEvent.Refresh) },
                        )
                    }

                state.operations.isEmpty() ->
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        EmptyContent("Операций не найдено", Modifier.fillMaxSize())
                    }

                else ->
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(state.operations, key = { it.id }) { OperationHistoryCard(it) }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
            }
        }
    }
}

@Composable
private fun OperationHistoryCard(op: StockOperation) {
    val (icon, accent) = op.operationType.iconAndColor()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(26.dp))
                    OperationTypeChip(op.operationType)
                }
                Text(
                    formatOperationDate(op.createdAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    op.createdByName ?: "Пользователь #${op.createdBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    op.warehouseName ?: "Склад #${op.warehouseId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            op.comment?.takeIf { it.isNotBlank() }?.let { c ->
                Text(
                    "Комментарий: $c",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            op.items.forEach { item ->
                ItemLine(item)
            }
        }
    }
}

@Composable
private fun ItemLine(item: StockOperationItem) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        val title = "${item.productName ?: "Товар"} (${item.productArticle ?: "—"})"
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        val qtyPrice =
            buildString {
                append("Кол-во: ${formatQuantity(item.quantity)}")
                item.price?.let { append(" · Цена: ${formatMoney(it)}") }
            }
        Text(qtyPrice, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        item.reason?.takeIf { it.isNotBlank() }?.let { r ->
            Text(
                "Причина: $r",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun OperationTypeChip(type: StockOperationType) {
    val label = type.ruLabel()
    val color = type.accentColor()
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.18f),
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun StockOperationType.accentColor(): Color =
    when (this) {
        StockOperationType.RECEIPT -> Color(0xFF198754)
        StockOperationType.ISSUE -> Color(0xFFE67E22)
        StockOperationType.WRITE_OFF -> Color(0xFFDC3545)
        StockOperationType.INVENTORY -> Color(0xFF0D6EFD)
    }

private fun StockOperationType.iconAndColor(): Pair<ImageVector, Color> =
    when (this) {
        StockOperationType.RECEIPT -> Icons.Filled.MoveToInbox to Color(0xFF198754)
        StockOperationType.ISSUE -> Icons.Filled.Output to Color(0xFFE67E22)
        StockOperationType.WRITE_OFF -> Icons.Filled.DeleteForever to Color(0xFFDC3545)
        StockOperationType.INVENTORY -> Icons.AutoMirrored.Filled.FactCheck to Color(0xFF0D6EFD)
    }

private fun formatOperationDate(iso: String): String {
    val tIdx = iso.indexOf('T')
    if (tIdx <= 0) return iso
    val timePart = iso.substring(tIdx + 1)
    val hhmm = timePart.take(5)
    return "${iso.substring(0, tIdx)} $hhmm"
}

private fun formatQuantity(q: Double): String {
    val s = String.format(Locale.US, "%.6f", q).trimEnd('0').trimEnd('.')
    return s.ifEmpty { "0" }
}

private fun formatMoney(v: Double): String = String.format(Locale.US, "%.2f", v)
