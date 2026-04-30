package com.example.warehouse_accounting_app.core.network

import com.example.warehouse_accounting_app.core.config.AppConfig
import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(authDataStore: AuthDataStore): HttpClient = HttpClient(Android) {
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                explicitNulls = false
            },
        )
    }
    install(Auth) {
        bearer {
            loadTokens {
                val token = authDataStore.getToken()
                if (token.isNullOrBlank()) {
                    null
                } else {
                    BearerTokens(accessToken = token, refreshToken = "")
                }
            }
        }
    }
    defaultRequest {
        url(AppConfig.API_BASE_URL)
    }
    expectSuccess = false
}
