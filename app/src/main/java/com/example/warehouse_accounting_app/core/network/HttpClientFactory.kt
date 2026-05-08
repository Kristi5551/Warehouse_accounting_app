package com.example.warehouse_accounting_app.core.network

import android.util.Log
import com.example.warehouse_accounting_app.BuildConfig
import com.example.warehouse_accounting_app.core.config.AppConfig
import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val AUTH_DIAG_TAG = "WarehouseAuth"

private fun isAuthAnonymousOnlyPath(builder: HttpRequestBuilder): Boolean {
    val segs = builder.url.pathSegments.filter { it.isNotEmpty() }
    val path = if (segs.isEmpty()) "/" else "/" + segs.joinToString("/")
    val p = path.trimEnd('/').lowercase()
    return p.endsWith("/api/auth/login") || p.endsWith("/api/auth/register")
}

fun createHttpClient(authDataStore: AuthDataStore): HttpClient {
    val client = HttpClient(Android) {
        logApiBaseUrl(AppConfig.BASE_URL)

        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
        }

        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d(NETWORK_LOG_TAG, message)
                    }
                }
                level = LogLevel.HEADERS
                sanitizeHeader { name ->
                    name.equals(HttpHeaders.Authorization, ignoreCase = true)
                }
            }
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
                
                
                sendWithoutRequest { request ->
                    !isAuthAnonymousOnlyPath(request)
                }
            }
        }

        expectSuccess = false
    }

    if (BuildConfig.DEBUG) {
        client.plugin(HttpSend).intercept { request ->
            val call = execute(request)
            val path = call.request.url.encodedPath
            if (path.contains("/api/auth/me", ignoreCase = true)) {
                val attached = call.request.headers.contains(HttpHeaders.Authorization)
                Log.d(AUTH_DIAG_TAG, "Authorization header attached = $attached")
            }
            call
        }
    }

    return client
}
