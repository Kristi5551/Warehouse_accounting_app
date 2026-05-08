package com.example.warehouse_accounting_app.core.util

import android.util.Log
import com.example.warehouse_accounting_app.BuildConfig

class AndroidAppLogger : AppLogger {
    override fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
}
