package com.example.warehouse_accounting_app.core.network

import android.util.Log
import com.example.warehouse_accounting_app.BuildConfig

/** Фильтр в Logcat: `WarehouseNet` или `adb logcat -s WarehouseNet`. */
const val NETWORK_LOG_TAG = "WarehouseNet"

fun logApiBaseUrl(baseUrl: String) {
    if (BuildConfig.DEBUG) {
        Log.d(NETWORK_LOG_TAG, "API base URL = $baseUrl")
    }
}

fun logApiException(e: ApiException, context: String) {
    Log.w(NETWORK_LOG_TAG, "[$context] HTTP ${e.statusCode}: ${e.message}")
}

/** Полный стектрейс сетевых сбоев (таймаут, обрыв, DNS и т.д.). */
fun logNetworkFailure(throwable: Throwable, context: String) {
    Log.e(NETWORK_LOG_TAG, "[$context] ${throwable.javaClass.simpleName}: ${throwable.message}", throwable)
}
