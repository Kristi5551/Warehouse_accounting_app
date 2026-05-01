package com.example.warehouse_accounting_app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    secondary = Slate40,
    onSecondary = Color.White,
    secondaryContainer = Slate90,
    onSecondaryContainer = Color(0xFF0F1D2C),
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = Slate90,
    onSurfaceVariant = Color(0xFF42474E),
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF72787E),
    outlineVariant = Color(0xFFC2C8CF),
)

@Composable
fun WarehouseAccountingTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
