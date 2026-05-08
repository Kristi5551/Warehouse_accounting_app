package com.example.warehouse_accounting_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.warehouse_accounting_app.core.navigation.AppRoutes
import com.example.warehouse_accounting_app.di.WarehouseViewModelFactory

@Composable
fun AppNavGraph(
    viewModelFactory: WarehouseViewModelFactory,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = AppRoutes.Splash, modifier = modifier) {
        warehouseAppGraph(navController, viewModelFactory)
    }
}
