package com.example.warehouse_accounting_app.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Некорректный или отсутствующий числовой id в deep link / аргументах навигации.
 */
@Composable
fun InvalidRouteArgumentScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Ошибка",
                onBack = onBack,
            )
        },
    ) { padding ->
        ErrorContent(
            message = "Некорректный параметр экрана",
            modifier = modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(horizontal = 8.dp),
            onRetry = null,
            onSecondaryAction = onBack,
            secondaryActionLabel = "Вернуться",
        )
    }
}
