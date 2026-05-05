package com.example.warehouse_accounting_app.core.util

import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * Значения `dateFrom` / `dateTo` в query к API: ISO **yyyy-MM-dd** (календарная дата без времени, как ожидает сервер).
 * Не локализованные строки; пустое поле = параметр не отправляется.
 *
 * Сервер включает **весь день `dateTo`**: условие `created_at < dateTo + 1 день` в локальной timezone JVM (см. `API_DATE_RANGE.md` на сервере).
 */
object IsoCalendarDateQuery {
    private val isoDate = Regex("""^\d{4}-\d{2}-\d{2}$""")

    /** Сообщение для UI или null, если поля пусты, формат верный и «от» не позже «до». */
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
                // формат уже проверен regex выше
            }
        }
        return null
    }
}
