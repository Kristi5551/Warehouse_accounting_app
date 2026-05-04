package com.example.warehouse_accounting_app.presentation.stock.receipt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppButton
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
) {
    val viewModel: ReceiptViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ReceiptEvent.Success -> {
                    snackbarHostState.showSnackbar("Приход товара оформлен")
                    onSuccess()
                }
                is ReceiptEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    AppScaffold(
        topBar = { AppTopBar(title = "Приход товара", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (state.isLoading) {
            LoadingContent()
            return@AppScaffold
        }
        var expanded by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()).imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = state.selectedProduct?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Товар *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            isError = state.productError != null,
                            shape = MaterialTheme.shapes.medium,
                            supportingText = state.productError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            state.products.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text("${p.name} (арт: ${p.article})") },
                                    onClick = { viewModel.onProductSelect(p); expanded = false },
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = state.quantity,
                        onValueChange = viewModel::onQuantityChange,
                        label = { Text("Количество *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.quantityError != null,
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        supportingText = state.quantityError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    )
                    OutlinedTextField(
                        value = state.price,
                        onValueChange = viewModel::onPriceChange,
                        label = { Text("Цена за единицу *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.priceError != null,
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        supportingText = state.priceError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    )
                    OutlinedTextField(
                        value = state.supplier,
                        onValueChange = viewModel::onSupplierChange,
                        label = { Text("Поставщик") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                    )
                    OutlinedTextField(
                        value = state.comment,
                        onValueChange = viewModel::onCommentChange,
                        label = { Text("Комментарий") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = MaterialTheme.shapes.medium,
                    )
                    if (state.errorMessage != null) {
                        Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                    AppButton(
                        text = if (state.isSaving) "Оформление…" else "Оформить приход",
                        onClick = viewModel::onSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving,
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
