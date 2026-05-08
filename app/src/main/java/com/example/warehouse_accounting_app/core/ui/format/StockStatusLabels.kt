package com.example.warehouse_accounting_app.core.ui.format

import com.example.warehouse_accounting_app.domain.model.StockStatus

fun StockStatus.ruLabel(): String = when (this) {
    StockStatus.IN_STOCK -> "В наличии"
    StockStatus.LOW_STOCK -> "Низкий остаток"
    StockStatus.OUT_OF_STOCK -> "Нет в наличии"
}
