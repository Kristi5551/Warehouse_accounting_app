package com.example.warehouse_accounting_app.presentation.operations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Output
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.warehouse_accounting_app.core.ui.format.ruLabel
import com.example.warehouse_accounting_app.core.util.NumberFormatters
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationItem
import com.example.warehouse_accounting_app.domain.model.StockOperationType

@Composable
internal fun OperationHistoryOperationCard(op: StockOperation) {
    val (icon, accent) = op.operationType.iconAndColor()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(26.dp))
                    OperationTypeChip(op.operationType)
                }
                Text(
                    formatOperationDate(op.createdAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    op.createdByName ?: "Пользователь #${op.createdBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    op.warehouseName ?: "Склад #${op.warehouseId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            op.comment?.takeIf { it.isNotBlank() }?.let { c ->
                Text(
                    "Комментарий: $c",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            op.items.forEach { item ->
                OperationHistoryItemLine(item)
            }
        }
    }
}

@Composable
private fun OperationHistoryItemLine(item: StockOperationItem) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        val title = "${item.productName ?: "Товар"} (${item.productArticle ?: "—"})"
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        val qtyPrice =
            buildString {
                append("Кол-во: ${NumberFormatters.quantityDisplay(item.quantity)}")
                item.price?.let { append(" · Цена: ${NumberFormatters.moneyAmountDisplay(it)}") }
            }
        Text(qtyPrice, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        item.reason?.takeIf { it.isNotBlank() }?.let { r ->
            Text(
                "Причина: $r",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun OperationTypeChip(type: StockOperationType) {
    val label = type.ruLabel()
    val color = type.accentColor()
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.18f),
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun StockOperationType.accentColor(): Color =
    when (this) {
        StockOperationType.RECEIPT -> Color(0xFF198754)
        StockOperationType.ISSUE -> Color(0xFFE67E22)
        StockOperationType.WRITE_OFF -> Color(0xFFDC3545)
        StockOperationType.INVENTORY -> Color(0xFF0D6EFD)
    }

private fun StockOperationType.iconAndColor(): Pair<ImageVector, Color> =
    when (this) {
        StockOperationType.RECEIPT -> Icons.Filled.MoveToInbox to Color(0xFF198754)
        StockOperationType.ISSUE -> Icons.Filled.Output to Color(0xFFE67E22)
        StockOperationType.WRITE_OFF -> Icons.Filled.DeleteForever to Color(0xFFDC3545)
        StockOperationType.INVENTORY -> Icons.AutoMirrored.Filled.FactCheck to Color(0xFF0D6EFD)
    }

private fun formatOperationDate(iso: String): String {
    val tIdx = iso.indexOf('T')
    if (tIdx <= 0) return iso
    val timePart = iso.substring(tIdx + 1)
    val hhmm = timePart.take(5)
    return "${iso.substring(0, tIdx)} $hhmm"
}
