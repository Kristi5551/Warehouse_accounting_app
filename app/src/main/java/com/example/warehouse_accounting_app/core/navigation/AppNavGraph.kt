package com.example.warehouse_accounting_app.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.core.ui.components.AppScaffold
import com.example.warehouse_accounting_app.core.ui.components.AppTopBar
import com.example.warehouse_accounting_app.presentation.auth.login.LoginScreen
import com.example.warehouse_accounting_app.presentation.auth.register.RegisterScreen
import com.example.warehouse_accounting_app.presentation.categories.CategoryEditScreen
import com.example.warehouse_accounting_app.presentation.categories.CategoryListScreen
import com.example.warehouse_accounting_app.presentation.dashboard.DashboardScreen
import com.example.warehouse_accounting_app.presentation.profile.ProfileScreen
import com.example.warehouse_accounting_app.presentation.splash.SplashDestination
import com.example.warehouse_accounting_app.presentation.splash.SplashScreenContent
import com.example.warehouse_accounting_app.presentation.splash.SplashViewModel
import com.example.warehouse_accounting_app.presentation.users.UserListScreen

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
            LaunchedEffect(Unit) { splashViewModel.startCheck() }
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
        composable(AppRoutes.Profile) {
            ProfileScreen(
                viewModelFactory = viewModelFactory,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppRoutes.Users) {
            UserListScreen(
                viewModelFactory = viewModelFactory,
                onBack = { navController.popBackStack() },
                onSessionExpired = {
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppRoutes.Categories) {
            CategoryListScreen(
                viewModelFactory = viewModelFactory,
                onNavigateToCreate = { navController.navigate(AppRoutes.CategoryNew) },
                onNavigateToEdit = { id -> navController.navigate(AppRoutes.categoryEdit(id)) },
                onBack = { navController.popBackStack() },
                onSessionExpired = {
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppRoutes.CategoryNew) {
            CategoryEditScreen(
                viewModelFactory = viewModelFactory,
                categoryId = null,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                onSessionExpired = {
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(
            AppRoutes.CategoryEdit,
            arguments = listOf(navArgument("id") { type = NavType.LongType }),
        ) { back ->
            val id = back.arguments?.getLong("id")
            CategoryEditScreen(
                viewModelFactory = viewModelFactory,
                categoryId = id,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                onSessionExpired = {
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppRoutes.Products) { Placeholder("Товары") { navController.popBackStack() } }
        composable(AppRoutes.ProductDetails) { Placeholder("Товар") { navController.popBackStack() } }
        composable(AppRoutes.ProductEdit) { Placeholder("Редактирование товара") { navController.popBackStack() } }
        composable(AppRoutes.StockBalances) { Placeholder("Остатки") { navController.popBackStack() } }
        composable(AppRoutes.Receipt) { Placeholder("Приход") { navController.popBackStack() } }
        composable(AppRoutes.Issue) { Placeholder("Расход") { navController.popBackStack() } }
        composable(AppRoutes.WriteOff) { Placeholder("Списание") { navController.popBackStack() } }
        composable(AppRoutes.Inventory) { Placeholder("Инвентаризация") { navController.popBackStack() } }
        composable(AppRoutes.OperationHistory) { Placeholder("История операций") { navController.popBackStack() } }
        composable(AppRoutes.Reports) { Placeholder("Отчёты") { navController.popBackStack() } }
    }
}

@Composable
private fun Placeholder(title: String, onBack: () -> Unit) {
    AppScaffold(
        topBar = { AppTopBar(title = title, onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Раздел «$title» в разработке",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
