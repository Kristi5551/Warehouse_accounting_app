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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppButton
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
    viewModelFactory: WarehouseViewModelFactory,
    productId: Long?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    onSessionExpired: () -> Unit,
) {
    val viewModel: ProductEditViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(productId) { if (productId != null) viewModel.loadProduct(productId) }
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProductEditEvent.SaveSuccess -> onSaved()
                is ProductEditEvent.ShowError -> {}
                ProductEditEvent.SessionExpired -> onSessionExpired()
            }
        }
    }

    AppScaffold(
        topBar = { AppTopBar(title = if (state.isEditMode) "Редактирование товара" else "Новый товар", onBack = onBack) },
    ) { padding ->
        when {
            state.isLoading -> LoadingContent()
            state.errorMessage != null && state.editingProduct == null -> ErrorContent(state.errorMessage!!)
            else -> {
                var categoryExpanded by remember { mutableStateOf(false) }
                val selectedCatName = state.categories.find { it.id == state.selectedCategoryId }?.name ?: ""

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Spacer(Modifier.height(4.dp))

                    OutlinedTextField(
                        value = state.article, onValueChange = viewModel::onArticleChange,
                        label = { Text("Артикул *") }, modifier = Modifier.fillMaxWidth(),
                        isError = state.articleError != null, singleLine = true, shape = MaterialTheme.shapes.medium,
                        supportingText = state.articleError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    )
                    OutlinedTextField(
                        value = state.name, onValueChange = viewModel::onNameChange,
                        label = { Text("Название *") }, modifier = Modifier.fillMaxWidth(),
                        isError = state.nameError != null, singleLine = true, shape = MaterialTheme.shapes.medium,
                        supportingText = state.nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    )

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = selectedCatName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Категория *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            isError = state.categoryError != null, shape = MaterialTheme.shapes.medium,
                            supportingText = state.categoryError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        )
                        ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                            state.categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = { viewModel.onCategoryChange(cat.id); categoryExpanded = false },
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.unit, onValueChange = viewModel::onUnitChange,
                        label = { Text("Единица измерения *") }, modifier = Modifier.fillMaxWidth(),
                        isError = state.unitError != null, singleLine = true, shape = MaterialTheme.shapes.medium,
                        supportingText = state.unitError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    )
                    OutlinedTextField(
                        value = state.purchasePrice, onValueChange = viewModel::onPurchasePriceChange,
                        label = { Text("Закупочная цена *") }, modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.purchasePriceError != null, singleLine = true, shape = MaterialTheme.shapes.medium,
                        supportingText = state.purchasePriceError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    )
                    OutlinedTextField(
                        value = state.salePrice, onValueChange = viewModel::onSalePriceChange,
                        label = { Text("Цена продажи *") }, modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.salePriceError != null, singleLine = true, shape = MaterialTheme.shapes.medium,
                        supportingText = state.salePriceError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    )
                    OutlinedTextField(
                        value = state.minStock, onValueChange = viewModel::onMinStockChange,
                        label = { Text("Минимальный остаток *") }, modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.minStockError != null, singleLine = true, shape = MaterialTheme.shapes.medium,
                        supportingText = state.minStockError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    )

                    if (state.isEditMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Активен", style = MaterialTheme.typography.bodyLarge)
                            Switch(checked = state.isActive, onCheckedChange = viewModel::onIsActiveChange)
                        }
                    }

                    if (state.errorMessage != null) {
                        Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }

                    AppButton(
                        text = if (state.isSaving) "Сохранение…" else "Сохранить",
                        onClick = viewModel::onSave,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving,
                    )
                    AppButton(
                        text = "Отмена",
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving,
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
