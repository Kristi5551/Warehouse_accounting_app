package com.example.warehouse_accounting_app.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorContent(message: String, modifier: Modifier = Modifier) {
    Text(text = message, modifier = modifier)
}
