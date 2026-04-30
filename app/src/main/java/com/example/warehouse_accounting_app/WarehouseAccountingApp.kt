package com.example.warehouse_accounting_app

import android.app.Application
import com.example.warehouse_accounting_app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WarehouseAccountingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WarehouseAccountingApp)
            modules(appModule)
        }
    }
}
