package com.example.warehouse_accounting_app.core.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatters {
    private val money = DecimalFormat("#0.00", DecimalFormatSymbols(Locale.US))
    private val qty = DecimalFormat("#0.###", DecimalFormatSymbols(Locale.US))

    fun money(value: Double): String = money.format(value)
    fun quantity(value: Double): String = qty.format(value)
}
