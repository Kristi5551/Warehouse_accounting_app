package com.example.warehouse_accounting_app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Токен недействителен или доступ к профилю закрыт (401/403 с [GET /api/auth/me]).
 * Токен к этому моменту уже сброшен в [AuthRepositoryImpl].
 */
@Composable
fun SessionExpiredScreen(
    modifier: Modifier = Modifier,
    onGoToLogin: () -> Unit,
) {
    AppScaffold(
        topBar = {
            AppTopBar(
                title = "Сессия",
                onBack = onGoToLogin,
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Login,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                modifier = Modifier.size(64.dp),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Сессия истекла. Войдите снова",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onGoToLogin,
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                Text("Перейти ко входу")
            }
        }
    }
}
