package com.example.warehouse_accounting_app.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.warehouse_accounting_app.core.ui.theme.ColorError
import com.example.warehouse_accounting_app.core.ui.theme.ColorErrorContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorSuccess
import com.example.warehouse_accounting_app.core.ui.theme.ColorSuccessContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorWarning
import com.example.warehouse_accounting_app.core.ui.theme.ColorWarningContainer
import com.example.warehouse_accounting_app.domain.model.UserStatus

@Composable
fun StatusChip(status: UserStatus) {
    val (text, bg, fg) = when (status) {
        UserStatus.ACTIVE -> Triple("Активен", ColorSuccessContainer, ColorSuccess)
        UserStatus.PENDING -> Triple("Ожидает", ColorWarningContainer, ColorWarning)
        UserStatus.BLOCKED -> Triple("Заблокирован", ColorErrorContainer, ColorError)
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
        )
    }
}
