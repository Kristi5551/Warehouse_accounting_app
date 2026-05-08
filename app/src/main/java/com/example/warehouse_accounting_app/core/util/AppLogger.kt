package com.example.warehouse_accounting_app.core.util

/**
 * Тонкая абстракция над платформенным логированием, чтобы слой data не импортировал [android.util.Log] / [BuildConfig].
 */
fun interface AppLogger {
    /** Сообщение уровня debug; в release обычно no-op. */
    fun d(tag: String, message: String)
}
