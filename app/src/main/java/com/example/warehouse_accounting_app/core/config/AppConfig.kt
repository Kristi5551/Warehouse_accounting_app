package com.example.warehouse_accounting_app.core.config

import com.example.warehouse_accounting_app.BuildConfig

object AppConfig {

    val BASE_URL: String = BuildConfig.API_BASE_URL.trimEnd('/')

    fun url(path: String): String {
        val p = path.trim().removePrefix("/")
        return "$BASE_URL/$p"
    }
}
