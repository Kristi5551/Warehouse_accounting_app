package com.example.warehouse_accounting_app.core.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatters {
    private val money = DecimalFormat("#0.00", DecimalFormatSymbols(Locale.US))
    private val qty = DecimalFormat("#0.###", DecimalFormatSymbols(Locale.US))

    fun money(value: Double): String = money.format(value)
    fun quantity(value: Double): String = qty.format(value)

    fun parseUserDecimal(raw: String): Double? {
        val t =
            raw.trim()
                .replace("\u00a0", "")
                .replace(" ", "")
                .replace(',', '.')
        if (t.isEmpty()) return null
        return t.toDoubleOrNull()
    }

    fun quantityDisplay(value: Double): String =
        localizeDecimalSeparator(
            normalizeForDisplay(value).stripTrailingZeros().toPlainString(),
        )

    fun moneyAmountDisplay(value: Double): String =
        localizeDecimalSeparator(
            BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
        )

    fun editableDecimal(value: Double, maxScale: Int): String =
        localizeDecimalSeparator(
            BigDecimal.valueOf(value).setScale(maxScale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
        )

    private fun normalizeForDisplay(value: Double): BigDecimal {
        if (value.isNaN() || value.isInfinite()) return BigDecimal.valueOf(0.0)
        return BigDecimal.valueOf(value).setScale(12, RoundingMode.HALF_UP).stripTrailingZeros()
    }

    private fun localizeDecimalSeparator(plainWithDot: String): String {
        val sep = DecimalFormatSymbols.getInstance().decimalSeparator
        return if (sep != '.') plainWithDot.replace('.', sep) else plainWithDot
    }
}
