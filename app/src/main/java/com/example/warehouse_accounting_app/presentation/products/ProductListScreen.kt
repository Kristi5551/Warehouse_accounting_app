package com.example.warehouse_accounting_app.presentation.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.core.ui.components.ConfirmDialog
import com.example.warehouse_accounting_app.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.presentation.products.components.ProductListBody
import com.example.warehouse_accounting_app.presentation.products.components.ProductListFiltersAndCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToDetails: (Long) -> Unit,
    onBack: () -> Unit,
    onSessionExpired: () -> Unit,
) {
    val viewModel: ProductListViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var productToDeactivate by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProductListEvent.NavigateToCreate -> onNavigateToCreate()
                is ProductListEvent.NavigateToEdit -> onNavigateToEdit(event.productId)
                is ProductListEvent.NavigateToDetails -> onNavigateToDetails(event.productId)
                is ProductListEvent.ShowSuccess -> snackbarHostState.showSnackbar(event.message)
                is ProductListEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                ProductListEvent.SessionExpired -> onSessionExpired()
            }
        }
    }
    LaunchedEffect(state.roleErrorMessage) {
        state.roleErrorMessage?.let { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(state.categoriesErrorMessage) {
        state.categoriesErrorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    productToDeactivate?.let { id ->
        val p = state.products.find { it.id == id }
        if (p != null) {
            ConfirmDialog(
                title = "Деактивировать товар",
                text = "Товар «${p.name}» будет деактивирован. Продолжить?",
                confirmText = "Деактивировать",
                onConfirm = { productToDeactivate = null; viewModel.onDeleteProduct(p) },
                onDismiss = { productToDeactivate = null },
            )
        }
    }

    AppScaffold(
        topBar = { AppTopBar(title = "Товары", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.isAdminUser) {
                FloatingActionButton(
                    onClick = { viewModel.onCreateClick() },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Добавить товар", tint = Color.White)
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            ProductListFiltersAndCategories(
                state = state,
                onSearchChange = viewModel::onSearchChange,
                onActiveOnlyChange = viewModel::onActiveOnlyChange,
                onRetryRole = { viewModel.retryUserRole() },
                onRetryCategories = { viewModel.retryCategories() },
                onCategoryFilter = { viewModel.onCategoryFilter(it) },
            )
            Spacer(Modifier.height(8.dp))
            ProductListBody(
                state = state,
                onLoadProducts = { viewModel.loadProducts() },
                onProductClick = { viewModel.onDetailsClick(it.id) },
                onEditClick = viewModel::onEditClick,
                onDeactivateClick = { productToDeactivate = it.id },
            )
        }
    }
}
