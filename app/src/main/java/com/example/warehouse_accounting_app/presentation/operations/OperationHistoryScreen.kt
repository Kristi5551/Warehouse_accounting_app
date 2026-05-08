package com.example.warehouse_accounting_app.presentation.operations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.presentation.operations.components.OperationHistoryFilters
import com.example.warehouse_accounting_app.presentation.operations.components.OperationHistoryListBody
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.UserPick

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
            OperationHistoryFilters(
                state = state,
                productFieldText = productFieldText,
                userFieldText = userFieldText,
                productMenuExpanded = productMenuExpanded,
                onProductMenuExpandedChange = { productMenuExpanded = it },
                userMenuExpanded = userMenuExpanded,
                onUserMenuExpandedChange = { userMenuExpanded = it },
                onEvent = viewModel::onEvent,
            )
            Spacer(Modifier.height(12.dp))
            OperationHistoryListBody(state = state, onEvent = viewModel::onEvent)
        }
    }
}
