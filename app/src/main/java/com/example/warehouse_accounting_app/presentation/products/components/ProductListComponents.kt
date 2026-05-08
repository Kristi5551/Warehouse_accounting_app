package com.example.warehouse_accounting_app.presentation.products.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.warehouse_accounting_app.core.ui.components.EmptyContent
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.core.util.NumberFormatters
import com.example.warehouse_accounting_app.presentation.products.ProductListState
import com.example.warehouse_accounting_app.presentation.products.filtered

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductListFiltersAndCategories(
    state: ProductListState,
    onSearchChange: (String) -> Unit,
    onActiveOnlyChange: (Boolean) -> Unit,
    onRetryRole: () -> Unit,
    onRetryCategories: () -> Unit,
    onCategoryFilter: (Long?) -> Unit,
) {
    OutlinedTextField(
        value = state.searchQuery,
        onValueChange = onSearchChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Поиск по названию или артикулу…") },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
    )
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("Только активные", style = MaterialTheme.typography.bodyMedium)
        Switch(checked = state.activeOnly, onCheckedChange = onActiveOnlyChange)
    }
    state.roleErrorMessage?.let { msg ->
        Spacer(Modifier.height(8.dp))
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.medium,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    msg,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                )
                TextButton(onClick = onRetryRole) {
                    Text("Повторить")
                }
            }
        }
    }
    when {
        state.isCategoriesLoading && state.categories.isEmpty() && state.categoriesErrorMessage == null -> {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        state.categoriesErrorMessage != null -> {
            Spacer(Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        state.categoriesErrorMessage!!,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TextButton(onClick = onRetryCategories) {
                        Text("Повторить")
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Фильтр по категориям недоступен",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        state.categories.isNotEmpty() -> {
            Spacer(Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.selectedCategoryId == null,
                        onClick = { onCategoryFilter(null) },
                        label = { Text("Все") },
                    )
                }
                items(state.categories) { cat ->
                    FilterChip(
                        selected = state.selectedCategoryId == cat.id,
                        onClick = { onCategoryFilter(if (state.selectedCategoryId == cat.id) null else cat.id) },
                        label = { Text(cat.name) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProductListBody(
    state: ProductListState,
    onLoadProducts: () -> Unit,
    onProductClick: (Product) -> Unit,
    onEditClick: (Long) -> Unit,
    onDeactivateClick: (Product) -> Unit,
) {
    when {
        state.isLoading -> LoadingContent()
        state.errorMessage != null -> ErrorContent(
            message = state.errorMessage!!,
            onRetry = onLoadProducts,
        )
        state.filtered.isEmpty() -> EmptyContent(if (state.searchQuery.isBlank() && state.selectedCategoryId == null) "Товаров пока нет" else "Ничего не найдено")
        else -> {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.filtered, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        isAdmin = state.isAdminUser,
                        onClick = { onProductClick(product) },
                        onEdit = { onEditClick(product.id) },
                        onDeactivate = { onDeactivateClick(product) },
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductCard(
    product: Product,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDeactivate: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Inventory2,
                contentDescription = null,
                tint = if (product.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "Арт: ${product.article} · ${product.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!product.categoryName.isNullOrBlank()) {
                    Text(
                        product.categoryName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProductPriceLabel("Закуп:", product.purchasePrice)
                    ProductPriceLabel("Продажа:", product.salePrice)
                }
                Text(
                    "Мин. остаток: ${NumberFormatters.quantityDisplay(product.minStock)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (!product.isActive) {
                    Text("Неактивен", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
            }
            if (isAdmin) {
                Column {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Изменить", tint = MaterialTheme.colorScheme.primary)
                    }
                    if (product.isActive) {
                        IconButton(onClick = onDeactivate) {
                            Icon(Icons.Filled.ToggleOff, contentDescription = "Деактивировать", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductPriceLabel(label: String, price: Double) {
    Text(
        "$label ${NumberFormatters.moneyAmountDisplay(price)} ₽",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface,
    )
}
