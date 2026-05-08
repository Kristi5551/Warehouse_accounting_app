package com.example.warehouse_accounting_app.presentation.operations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.warehouse_accounting_app.core.ui.components.EmptyContent
import com.example.warehouse_accounting_app.core.ui.components.ErrorContent
import com.example.warehouse_accounting_app.core.ui.components.LoadingContent
import com.example.warehouse_accounting_app.presentation.operations.OperationHistoryEvent
import com.example.warehouse_accounting_app.presentation.operations.OperationHistoryState

@Composable
internal fun ColumnScope.OperationHistoryListBody(
    state: OperationHistoryState,
    onEvent: (OperationHistoryEvent) -> Unit,
) {
    when {
        state.isLoading ->
            Box(Modifier.weight(1f).fillMaxWidth()) {
                LoadingContent(Modifier.fillMaxSize())
            }

        state.errorMessage != null ->
            Column(Modifier.weight(1f).fillMaxWidth()) {
                ErrorContent(
                    message = state.errorMessage!!,
                    onRetry = { onEvent(OperationHistoryEvent.Refresh) },
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
                items(state.operations, key = { it.id }) { OperationHistoryOperationCard(it) }
                item { Spacer(Modifier.height(16.dp)) }
            }
    }
}
