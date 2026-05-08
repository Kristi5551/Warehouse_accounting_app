package com.example.warehouse_accounting_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.warehouse_accounting_app.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.presentation.navigation.AppNavGraph
import com.example.warehouse_accounting_app.core.ui.theme.WarehouseAccountingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarehouseAccountingTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val factory = remember {
                        WarehouseViewModelFactory((application as WarehouseAccountingApp).container)
                    }
                    AppNavGraph(viewModelFactory = factory)
                }
            }
        }
    }
}
