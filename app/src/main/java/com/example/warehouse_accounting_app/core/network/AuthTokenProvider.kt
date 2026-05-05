package com.example.warehouse_accounting_app.core.network

fun interface gAuthTokenProvider {
    suspend fun getToken(): String?
}
