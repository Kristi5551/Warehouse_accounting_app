package com.example.warehouse_accounting_app.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.warehouse_accounting_app.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    viewModelFactory: WarehouseViewModelFactory,
    productId: Long,
    onBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onSessionExpired: () -> Unit,
) {
    val viewModel: ProductListViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(productId) { viewModel.loadProducts() }

    val product = state.products.find { it.id == productId }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Товар",
                onBack = onBack,
                actions = {
                    if (viewModel.isAdmin() && product != null) {
                        IconButton(onClick = { onNavigateToEdit(productId) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Изменить", tint = Color.White)
                        }
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> LoadingContent()
            product == null && state.errorMessage != null -> ErrorContent(state.errorMessage!!)
            product == null -> LoadingContent()
            else -> ProductDetailsContent(product, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
private fun ProductDetailsContent(product: Product, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                DetailRow("Закупочная цена", "${"%,.2f".format(product.purchasePrice)} ₽")
                DetailRow("Цена продажи", "${"%,.2f".format(product.salePrice)} ₽")
                DetailRow("Минимальный остаток", "${product.minStock} ${product.unit}")
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
