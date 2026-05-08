package com.example.warehouse_accounting_app

import android.app.Application
import com.example.warehouse_accounting_app.di.AppContainer

class WarehouseAccountingApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
