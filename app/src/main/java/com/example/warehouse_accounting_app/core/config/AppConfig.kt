package com.example.warehouse_accounting_app.core.config

import com.example.warehouse_accounting_app.BuildConfig

object AppConfig {
    /**
     * Базовый URL без завершающего слэша.
     *
     * **Debug:** `api.base.url` в `local.properties` или по умолчанию `http://10.0.2.2:8080` (эмулятор → хост).
     * В debug-сборке в манифесте разрешён cleartext HTTP (`cleartextTraffic=true`).
     *
     * **Release:** только `api.base.url` с **HTTPS**; cleartext в манифесте отключён (`cleartextTraffic=false`).
     */
    val BASE_URL: String = BuildConfig.API_BASE_URL.trimEnd('/')

    /**
     * Собирает абсолютный URL запроса. Устраняет ошибки склейки путей в Ktor Client на Android.
     */
    fun url(path: String): String {
        val p = path.trim().removePrefix("/")
        return "$BASE_URL/$p"
    }
}
