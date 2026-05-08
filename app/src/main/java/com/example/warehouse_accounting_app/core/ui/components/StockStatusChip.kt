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
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockInStock
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockInStockContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockLow
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockLowContainer
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockOut
import com.example.warehouse_accounting_app.core.ui.theme.ColorStockOutContainer
import com.example.warehouse_accounting_app.domain.model.StockStatus

@Composable
fun StockStatusChip(status: StockStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        StockStatus.IN_STOCK -> ColorStockInStockContainer to ColorStockInStock
        StockStatus.LOW_STOCK -> ColorStockLowContainer to ColorStockLow
        StockStatus.OUT_OF_STOCK -> ColorStockOutContainer to ColorStockOut
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
        modifier = modifier,
    ) {
        Text(
            text = status.ruLabel(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
        )
    }
}
