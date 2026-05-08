package com.example.warehouse_accounting_app.presentation.operations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.warehouse_accounting_app.core.ui.format.ruLabel
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.presentation.operations.OperationHistoryEvent
import com.example.warehouse_accounting_app.presentation.operations.OperationHistoryState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OperationHistoryFilters(
    state: OperationHistoryState,
    productFieldText: String,
    userFieldText: String,
    productMenuExpanded: Boolean,
    onProductMenuExpandedChange: (Boolean) -> Unit,
    userMenuExpanded: Boolean,
    onUserMenuExpandedChange: (Boolean) -> Unit,
    onEvent: (OperationHistoryEvent) -> Unit,
) {
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
                        onClick = { onEvent(OperationHistoryEvent.TypeFilterChanged(null)) },
                        label = { Text("Все") },
                    )
                }
                items(StockOperationType.entries.toList()) { type ->
                    FilterChip(
                        selected = state.typeFilter == type,
                        onClick = {
                            onEvent(
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
                onExpandedChange = onProductMenuExpandedChange,
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
                    onDismissRequest = { onProductMenuExpandedChange(false) },
                ) {
                    DropdownMenuItem(
                        text = { Text("Все товары") },
                        onClick = {
                            onEvent(OperationHistoryEvent.ProductFilterChanged(null))
                            onProductMenuExpandedChange(false)
                        },
                    )
                    state.products.forEach { p ->
                        DropdownMenuItem(
                            text = { Text("${p.name} (арт. ${p.article})") },
                            onClick = {
                                onEvent(OperationHistoryEvent.ProductFilterChanged(p.id))
                                onProductMenuExpandedChange(false)
                            },
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = userMenuExpanded,
                onExpandedChange = onUserMenuExpandedChange,
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
                    onDismissRequest = { onUserMenuExpandedChange(false) },
                ) {
                    DropdownMenuItem(
                        text = { Text("Все сотрудники") },
                        onClick = {
                            onEvent(OperationHistoryEvent.UserFilterChanged(null))
                            onUserMenuExpandedChange(false)
                        },
                    )
                    state.filterUsers.forEach { u ->
                        DropdownMenuItem(
                            text = { Text(u.fullName) },
                            onClick = {
                                onEvent(OperationHistoryEvent.UserFilterChanged(u.id))
                                onUserMenuExpandedChange(false)
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
                    onValueChange = { onEvent(OperationHistoryEvent.DateFromChanged(it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text("С даты") },
                    placeholder = { Text("ГГГГ-ММ-ДД") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )
                OutlinedTextField(
                    value = state.dateToInput,
                    onValueChange = { onEvent(OperationHistoryEvent.DateToChanged(it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text("По дату") },
                    placeholder = { Text("ГГГГ-ММ-ДД") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )
            }
            Button(
                onClick = { onEvent(OperationHistoryEvent.ApplyFilters) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Применить")
            }
        }
    }
}
