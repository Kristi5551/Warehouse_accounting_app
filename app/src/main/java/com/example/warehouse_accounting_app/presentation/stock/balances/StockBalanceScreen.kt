package com.example.warehouse_accounting_app.presentation.stock.balances

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.presentation.stock.balances.components.LowStockExplanationText
import com.example.warehouse_accounting_app.presentation.stock.balances.components.LowStockListSection
import com.example.warehouse_accounting_app.presentation.stock.balances.components.LowStockSearchField
import com.example.warehouse_accounting_app.presentation.stock.balances.components.StockBalanceListSection
import com.example.warehouse_accounting_app.presentation.stock.balances.components.StockBalanceSearchAndStatusFilters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockBalanceScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
) {
    val viewModel: StockBalanceViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Остатки",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { viewModel.onEvent(StockBalanceEvent.Refresh) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить", tint = Color.White)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            StockBalanceSearchAndStatusFilters(state = state, onEvent = viewModel::onEvent)
            Spacer(Modifier.height(8.dp))
            StockBalanceListSection(state = state, onEvent = viewModel::onEvent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LowStockScreen(
    viewModelFactory: WarehouseViewModelFactory,
    onBack: () -> Unit,
) {
    val viewModel: LowStockViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val list = state.filteredForLow

    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Низкие остатки",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { viewModel.onEvent(StockBalanceEvent.Refresh) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить", tint = Color.White)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(8.dp))
            LowStockSearchField(state = state, onEvent = viewModel::onEvent)
            Spacer(Modifier.height(8.dp))
            LowStockExplanationText()
            Spacer(Modifier.height(8.dp))
            LowStockListSection(state = state, balancesToShow = list, onEvent = viewModel::onEvent)
        }
    }
}
