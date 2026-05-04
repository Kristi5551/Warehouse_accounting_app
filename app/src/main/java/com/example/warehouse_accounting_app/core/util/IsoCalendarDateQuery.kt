package com.example.warehouse_accounting_app.core.util

/**
 * Значения `dateFrom` / `dateTo` в query к API: ISO **yyyy-MM-dd** (календарная дата без времени, как ожидает сервер).
 * Не локализованные строки; пустое поле = параметр не отправляется.
 */
object IsoCalendarDateQuery {
    private val isoDate = Regex("""^\d{4}-\d{2}-\d{2}$""")

    /** Сообщение для UI или null, если оба поля пусты или соответствуют ISO. */
    fun validationMessage(dateFromInput: String, dateToInput: String): String? {
        val from = dateFromInput.trim()
        val to = dateToInput.trim()
        if (from.isNotEmpty() && !isoDate.matches(from)) {
            return "Дата «от»: укажите в формате ГГГГ-ММ-ДД"
        }
        if (to.isNotEmpty() && !isoDate.matches(to)) {
            return "Дата «до»: укажите в формате ГГГГ-ММ-ДД"
        }
        return null
    }
}
