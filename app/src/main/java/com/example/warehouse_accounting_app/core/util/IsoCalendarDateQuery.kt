package com.example.warehouse_accounting_app.core.util

import java.time.LocalDate
import java.time.format.DateTimeParseException

object IsoCalendarDateQuery {
    private val isoDate = Regex("""^\d{4}-\d{2}-\d{2}$""")

    fun validationMessage(dateFromInput: String, dateToInput: String): String? {
        val from = dateFromInput.trim()
        val to = dateToInput.trim()
        if (from.isNotEmpty() && !isoDate.matches(from)) {
            return "Дата «от»: укажите в формате ГГГГ-ММ-ДД"
        }
        if (to.isNotEmpty() && !isoDate.matches(to)) {
            return "Дата «до»: укажите в формате ГГГГ-ММ-ДД"
        }
        if (from.isNotEmpty() && to.isNotEmpty()) {
            try {
                if (LocalDate.parse(from) > LocalDate.parse(to)) {
                    return "Дата «от» не может быть позже «до»"
                }
            } catch (_: DateTimeParseException) {
            }
        }
        return null
    }
}
