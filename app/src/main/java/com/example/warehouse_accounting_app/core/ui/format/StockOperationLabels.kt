package com.example.warehouse_accounting_app.core.ui.format

import com.example.warehouse_accounting_app.domain.model.StockOperationType

fun StockOperationType.ruLabel(): String =
    when (this) {
        StockOperationType.RECEIPT -> "Приход"
        StockOperationType.ISSUE -> "Расход"
        StockOperationType.WRITE_OFF -> "Списание"
        StockOperationType.INVENTORY -> "Инвентаризация"
    }
