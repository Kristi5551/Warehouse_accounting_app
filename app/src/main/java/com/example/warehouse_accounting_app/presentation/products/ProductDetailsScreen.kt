package com.example.warehouse_accounting_app.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Output
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.core.ui.format.ruLabel
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    viewModelFactory: WarehouseViewModelFactory,
    productId: Long,
    onBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onSessionExpired: () -> Unit,
) {
    val viewModel: ProductDetailsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Загружаем товар по id через GET /api/products/{id} при каждом открытии экрана
    LaunchedEffect(productId) { viewModel.loadProduct(productId) }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Товар",
                onBack = onBack,
                actions = {
                    if (state.isAdmin && state.product != null) {
                        IconButton(onClick = { onNavigateToEdit(productId) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Изменить", tint = Color.White)
                        }
                    }
                },
            )
        },
    ) { padding ->
        val productLoadError = state.productErrorMessage
        when {
            state.isProductLoading && state.product == null ->
                LoadingContent(Modifier.padding(padding))
            productLoadError != null && state.product == null ->
                ErrorContent(
                    message = productLoadError,
                    modifier = Modifier.padding(padding),
                    onRetry = { viewModel.loadProduct(productId) },
                )
            state.product != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                        .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Spacer(Modifier.height(4.dp))
                    ProductInfoCard(state.product!!)
                    ProductHistorySection(
                        history = state.history,
                        isLoading = state.isHistoryLoading,
                        historyErrorMessage = state.historyErrorMessage,
                        onRetryHistory = { viewModel.retryHistory() },
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ProductInfoCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    if (product.isActive) "Активен" else "Неактивен",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (product.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                )
            }
            HorizontalDivider()
            DetailRow("Артикул", product.article)
            DetailRow("Категория", product.categoryName ?: "—")
            DetailRow("Единица измерения", product.unit)
            DetailRow("Закупочная цена", "${"%.2f".format(product.purchasePrice)} ₽")
            DetailRow("Цена продажи", "${"%.2f".format(product.salePrice)} ₽")
            DetailRow("Минимальный остаток", "${product.minStock} ${product.unit}")
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.45f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.55f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ProductHistorySection(
    history: List<StockOperation>,
    isLoading: Boolean,
    historyErrorMessage: String?,
    onRetryHistory: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "История операций",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            HorizontalDivider()
            when {
                historyErrorMessage != null -> {
                    Text(
                        historyErrorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Button(onClick = onRetryHistory, modifier = Modifier.fillMaxWidth()) {
                        Text("Повторить")
                    }
                }
                isLoading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
                history.isEmpty() -> {
                    Text(
                        "История операций по товару отсутствует",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
                else -> {
                    history.forEach { op ->
                        HistoryRow(op)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(op: StockOperation) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = op.operationType.icon(),
            contentDescription = null,
            tint = op.operationType.tint(),
            modifier = Modifier.size(20.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                op.operationType.ruLabel(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = op.operationType.tint(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val qty = op.items.sumOf { it.quantity }
            Text(
                if (qty % 1.0 == 0.0) qty.toLong().toString() else "%.3f".format(qty),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            op.createdAt.take(10),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun StockOperationType.icon(): ImageVector = when (this) {
    StockOperationType.RECEIPT -> Icons.Filled.MoveToInbox
    StockOperationType.ISSUE -> Icons.Filled.Output
    StockOperationType.WRITE_OFF -> Icons.Filled.DeleteForever
    StockOperationType.INVENTORY -> Icons.AutoMirrored.Filled.FactCheck
}

@Composable
private fun StockOperationType.tint(): Color = when (this) {
    StockOperationType.RECEIPT -> Color(0xFF2E7D32)
    StockOperationType.ISSUE -> Color(0xFF1565C0)
    StockOperationType.WRITE_OFF -> MaterialTheme.colorScheme.error
    StockOperationType.INVENTORY -> Color(0xFFF57F17)
}
