package com.example.warehouse_accounting_app.presentation.auth.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.warehouse_accounting_app.core.ui.components.AppButton

@Composable
fun RegisterScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Регистрация", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text("Заглушка: форма будет подключена к RegisterUseCase.")
        Spacer(Modifier.height(16.dp))
        AppButton("Назад", onClick = onBack)
    }
}
