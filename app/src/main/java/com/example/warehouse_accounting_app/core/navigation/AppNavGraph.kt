package com.example.warehouse_accounting_app.core.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppButton
import com.example.warehouse_accounting_app.presentation.auth.login.LoginScreen
import com.example.warehouse_accounting_app.presentation.auth.register.RegisterScreen
import com.example.warehouse_accounting_app.presentation.dashboard.DashboardScreen
import com.example.warehouse_accounting_app.presentation.splash.SplashDestination
import com.example.warehouse_accounting_app.presentation.splash.SplashScreenContent
import com.example.warehouse_accounting_app.presentation.splash.SplashViewModel

@Composable
fun AppNavGraph(
    viewModelFactory: WarehouseViewModelFactory,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Splash,
        modifier = modifier,
    ) {
        composable(AppRoutes.Splash) {
            val splashViewModel: SplashViewModel = viewModel(factory = viewModelFactory)
            val destination by splashViewModel.destination.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                splashViewModel.startCheck()
            }
            LaunchedEffect(destination) {
                when (destination) {
                    SplashDestination.Login ->
                        navController.navigate(AppRoutes.Login) {
                            popUpTo(AppRoutes.Splash) { inclusive = true }
                        }
                    SplashDestination.Dashboard ->
                        navController.navigate(AppRoutes.Dashboard) {
                            popUpTo(AppRoutes.Splash) { inclusive = true }
                        }
                    null -> Unit
                }
            }
            SplashScreenContent()
        }
        composable(AppRoutes.Login) {
            LoginScreen(
                viewModelFactory = viewModelFactory,
                onLoggedIn = {
                    navController.navigate(AppRoutes.Dashboard) {
                        popUpTo(AppRoutes.Login) { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(AppRoutes.Register) },
            )
        }
        composable(AppRoutes.Register) {
            RegisterScreen(
                viewModelFactory = viewModelFactory,
                onBackToLogin = { navController.popBackStack() },
            )
        }
        composable(AppRoutes.Dashboard) {
            DashboardScreen(
                viewModelFactory = viewModelFactory,
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppRoutes.Profile) { Placeholder("Профиль") { navController.popBackStack() } }
        composable(AppRoutes.Users) { Placeholder("Пользователи") { navController.popBackStack() } }
        composable(AppRoutes.Categories) { Placeholder("Категории") { navController.popBackStack() } }
        composable(AppRoutes.CategoryEdit) { Placeholder("Категория") { navController.popBackStack() } }
        composable(AppRoutes.Products) { Placeholder("Товары") { navController.popBackStack() } }
        composable(AppRoutes.ProductDetails) { Placeholder("Товар") { navController.popBackStack() } }
        composable(AppRoutes.ProductEdit) { Placeholder("Редактирование товара") { navController.popBackStack() } }
        composable(AppRoutes.StockBalances) { Placeholder("Остатки") { navController.popBackStack() } }
        composable(AppRoutes.Receipt) { Placeholder("Приход") { navController.popBackStack() } }
        composable(AppRoutes.Issue) { Placeholder("Расход") { navController.popBackStack() } }
        composable(AppRoutes.WriteOff) { Placeholder("Списание") { navController.popBackStack() } }
        composable(AppRoutes.Inventory) { Placeholder("Инвентаризация") { navController.popBackStack() } }
        composable(AppRoutes.OperationHistory) { Placeholder("История операций") { navController.popBackStack() } }
        composable(AppRoutes.Reports) { Placeholder("Отчеты") { navController.popBackStack() } }
    }
}

@Composable
private fun Placeholder(title: String, onBack: () -> Unit) {
    Column(Modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        AppButton("Назад", onClick = onBack)
    }
}
