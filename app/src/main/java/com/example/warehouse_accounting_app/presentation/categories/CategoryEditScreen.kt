package com.example.warehouse_accounting_app.presentation.categories

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun CategoryEditScreen(
    viewModelFactory: WarehouseViewModelFactory,
    categoryId: Long?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    onSessionExpired: () -> Unit,
) {
    val viewModel: CategoryEditViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(categoryId) {
        if (categoryId != null) viewModel.loadCategory(categoryId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CategoryEditEvent.SaveSuccess -> onSaved()
                is CategoryEditEvent.ShowError -> { /* snackbar уже в state */ }
                CategoryEditEvent.SessionExpired -> onSessionExpired()
            }
        }
    }

    val title = if (state.isEditMode) "Редактирование категории" else "Новая категория"

    AppScaffold(
        topBar = { AppTopBar(title = title, onBack = onBack) },
    ) { padding ->
        when {
            state.isLoading -> LoadingContent()
            state.errorMessage != null && state.editingCategory == null -> ErrorContent(state.errorMessage!!)
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text("Название *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.nameError != null,
                        supportingText = state.nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                    )

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        shape = MaterialTheme.shapes.medium,
                    )

                    if (state.isEditMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "Активна",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Switch(
                                checked = state.isActive,
                                onCheckedChange = viewModel::onIsActiveChange,
                            )
                        }
                    }

                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Spacer(Modifier.height(8.dp))

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
