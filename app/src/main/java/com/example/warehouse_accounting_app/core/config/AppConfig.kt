package com.example.warehouse_accounting_app.core.config

import com.example.warehouse_accounting_app.BuildConfig

object AppConfig {
    /** Базовый URL без завершающего слэша (из local.properties → api.base.url или эмулятор по умолчанию). */
    val BASE_URL: String = BuildConfig.API_BASE_URL.trimEnd('/')

    /**
     * Собирает абсолютный URL запроса. Устраняет ошибки склейки путей в Ktor Client на Android.
     */
    fun url(path: String): String {
        val p = path.trim().removePrefix("/")
        return "$BASE_URL/$p"
    }
}
