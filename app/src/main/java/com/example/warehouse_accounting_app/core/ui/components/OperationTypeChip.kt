package com.example.warehouse_accounting_app.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.warehouse_accounting_app.core.ui.format.ruLabel
import com.example.warehouse_accounting_app.core.ui.theme.ColorOpInventory
import com.example.warehouse_accounting_app.core.ui.theme.ColorOpInventoryContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorOpIssue
import com.example.warehouse_accounting_app.core.ui.theme.ColorOpIssueContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorOpReceipt
import com.example.warehouse_accounting_app.core.ui.theme.ColorOpReceiptContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorOpWriteOff
import com.example.warehouse_accounting_app.core.ui.theme.ColorOpWriteOffContainer
import com.example.warehouse_accounting_app.domain.model.StockOperationType

/** Chip для отображения типа складской операции в списках и карточках. */
@Composable
fun OperationTypeChip(type: StockOperationType, modifier: Modifier = Modifier) {
    val (bg, fg) = when (type) {
        StockOperationType.RECEIPT -> ColorOpReceiptContainer to ColorOpReceipt
        StockOperationType.ISSUE -> ColorOpIssueContainer to ColorOpIssue
        StockOperationType.WRITE_OFF -> ColorOpWriteOffContainer to ColorOpWriteOff
        StockOperationType.INVENTORY -> ColorOpInventoryContainer to ColorOpInventory
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
        modifier = modifier,
    ) {
        Text(
            text = type.ruLabel(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
        )
    }
}
