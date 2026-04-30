package com.example.warehouse_accounting_app.core.network

fun interface AuthTokenProvider {
    suspend fun getToken(): String?
}
