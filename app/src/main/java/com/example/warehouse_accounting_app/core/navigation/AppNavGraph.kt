package com.example.warehouse_accounting_app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.warehouse_accounting_app.core.di.WarehouseViewModelFactory
import com.example.warehouse_accounting_app.domain.model.RolePermissions
import com.example.warehouse_accounting_app.presentation.auth.login.LoginScreen
import com.example.warehouse_accounting_app.presentation.auth.register.RegisterScreen
import com.example.warehouse_accounting_app.core.ui.components.InvalidRouteArgumentScreen
import com.example.warehouse_accounting_app.presentation.categories.CategoryEditScreen
import com.example.warehouse_accounting_app.presentation.categories.CategoryListScreen
import com.example.warehouse_accounting_app.presentation.dashboard.DashboardScreen
import com.example.warehouse_accounting_app.presentation.operations.OperationHistoryScreen
import com.example.warehouse_accounting_app.presentation.products.ProductDetailsScreen
import com.example.warehouse_accounting_app.presentation.products.ProductEditScreen
import com.example.warehouse_accounting_app.presentation.products.ProductListScreen
import com.example.warehouse_accounting_app.presentation.profile.ProfileScreen
import com.example.warehouse_accounting_app.presentation.reports.ReportsScreen
import com.example.warehouse_accounting_app.presentation.splash.SplashDestination
import com.example.warehouse_accounting_app.presentation.splash.SplashScreenContent
import com.example.warehouse_accounting_app.presentation.splash.SplashViewModel
import com.example.warehouse_accounting_app.presentation.stock.inventory.InventoryScreen
import com.example.warehouse_accounting_app.presentation.stock.issue.IssueScreen
import com.example.warehouse_accounting_app.presentation.stock.balances.LowStockScreen
import com.example.warehouse_accounting_app.presentation.stock.balances.StockBalanceScreen
import com.example.warehouse_accounting_app.presentation.stock.receipt.ReceiptScreen
import com.example.warehouse_accounting_app.presentation.stock.writeoff.WriteOffScreen
import com.example.warehouse_accounting_app.presentation.users.UserListScreen

private fun NavHostController.logout() {
    navigate(AppRoutes.Login) { popUpTo(graph.id) { inclusive = true }; launchSingleTop = true }
}

@Composable
fun AppNavGraph(
    viewModelFactory: WarehouseViewModelFactory,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = AppRoutes.Splash, modifier = modifier) {

        // ── Splash ────────────────────────────────────────────────────────────
        composable(AppRoutes.Splash) {
            val vm: SplashViewModel = viewModel(factory = viewModelFactory)
            val destination by vm.destination.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) { vm.startCheck() }
            LaunchedEffect(destination) {
                when (destination) {
                    SplashDestination.Login ->
                        navController.navigate(AppRoutes.Login) { popUpTo(AppRoutes.Splash) { inclusive = true } }
                    SplashDestination.Dashboard ->
                        navController.navigate(AppRoutes.Dashboard) { popUpTo(AppRoutes.Splash) { inclusive = true } }
                    is SplashDestination.Error, null -> Unit
                }
            }
            val errorMessage = (destination as? SplashDestination.Error)?.message
            SplashScreenContent(
                error = errorMessage,
                onRetry = { vm.startCheck() },
                onLoginAnyway = {
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(AppRoutes.Splash) { inclusive = true }
                    }
                },
            )
        }

        // ── Auth ──────────────────────────────────────────────────────────────
        composable(AppRoutes.Login) {
            LoginScreen(
                viewModelFactory = viewModelFactory,
                onLoggedIn = { navController.navigate(AppRoutes.Dashboard) { popUpTo(AppRoutes.Login) { inclusive = true } } },
                onRegister = { navController.navigate(AppRoutes.Register) },
            )
        }

        composable(AppRoutes.Register) {
            RegisterScreen(viewModelFactory = viewModelFactory, onBackToLogin = { navController.popBackStack() })
        }

        // ── Dashboard ─────────────────────────────────────────────────────────
        composable(AppRoutes.Dashboard) {
            DashboardScreen(
                viewModelFactory = viewModelFactory,
                onNavigate = { navController.navigate(it) },
                onLogout = { navController.logout() },
            )
        }

        // ── Profile (все роли) ────────────────────────────────────────────────
        composable(AppRoutes.Profile) {
            ProfileScreen(
                viewModelFactory = viewModelFactory,
                onBack = { navController.popBackStack() },
                onLogout = { navController.logout() },
            )
        }

        // ── Users (только ADMIN) ──────────────────────────────────────────────
        composable(AppRoutes.Users) {
            RoleGuard(
                viewModelFactory = viewModelFactory,
                allowed = { RolePermissions.canOpenUsers(it) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            ) {
                UserListScreen(
                    viewModelFactory = viewModelFactory,
                    onBack = { navController.popBackStack() },
                    onSessionExpired = { navController.logout() },
                )
            }
        }

        // ── Categories ────────────────────────────────────────────────────────
        // Список — все роли; создание и редактирование — только ADMIN (guard на edit-экранах)
        composable(AppRoutes.Categories) {
            CategoryListScreen(
                viewModelFactory = viewModelFactory,
                onNavigateToCreate = { navController.navigate(AppRoutes.CategoryNew) },
                onNavigateToEdit = { navController.navigate(AppRoutes.categoryEdit(it)) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            )
        }
        composable(AppRoutes.CategoryNew) {
            RoleGuard(
                viewModelFactory = viewModelFactory,
                allowed = { RolePermissions.canEditCategories(it) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            ) {
                CategoryEditScreen(
                    viewModelFactory = viewModelFactory,
                    categoryId = null,
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                    onSessionExpired = { navController.logout() },
                )
            }
        }
        composable(
            AppRoutes.CategoryEdit,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { back ->
            val raw = back.arguments?.getString("id")
            val id = raw?.toLongOrNull()?.takeIf { it > 0 }
            if (id == null) {
                InvalidRouteArgumentScreen(onBack = { navController.popBackStack() })
            } else {
                RoleGuard(
                    viewModelFactory = viewModelFactory,
                    allowed = { RolePermissions.canEditCategories(it) },
                    onBack = { navController.popBackStack() },
                    onSessionExpired = { navController.logout() },
                ) {
                    CategoryEditScreen(
                        viewModelFactory = viewModelFactory,
                        categoryId = id,
                        onSaved = { navController.popBackStack() },
                        onBack = { navController.popBackStack() },
                        onSessionExpired = { navController.logout() },
                    )
                }
            }
        }

        // ── Products ──────────────────────────────────────────────────────────
        // Список и детали — все роли; создание и редактирование — только ADMIN
        composable(AppRoutes.Products) {
            ProductListScreen(
                viewModelFactory = viewModelFactory,
                onNavigateToCreate = { navController.navigate(AppRoutes.ProductNew) },
                onNavigateToEdit = { navController.navigate(AppRoutes.productEdit(it)) },
                onNavigateToDetails = { navController.navigate(AppRoutes.productDetails(it)) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            )
        }
        composable(AppRoutes.ProductNew) {
            RoleGuard(
                viewModelFactory = viewModelFactory,
                allowed = { RolePermissions.canEditProducts(it) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            ) {
                ProductEditScreen(
                    viewModelFactory = viewModelFactory,
                    productId = null,
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                    onSessionExpired = { navController.logout() },
                )
            }
        }
        composable(
            AppRoutes.ProductDetails,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { back ->
            val raw = back.arguments?.getString("id")
            val id = raw?.toLongOrNull()?.takeIf { it > 0 }
            if (id == null) {
                InvalidRouteArgumentScreen(onBack = { navController.popBackStack() })
            } else {
                ProductDetailsScreen(
                    viewModelFactory = viewModelFactory,
                    productId = id,
                    onBack = { navController.popBackStack() },
                    onNavigateToEdit = { navController.navigate(AppRoutes.productEdit(it)) },
                    onSessionExpired = { navController.logout() },
                )
            }
        }
        composable(
            AppRoutes.ProductEdit,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { back ->
            val raw = back.arguments?.getString("id")
            val id = raw?.toLongOrNull()?.takeIf { it > 0 }
            if (id == null) {
                InvalidRouteArgumentScreen(onBack = { navController.popBackStack() })
            } else {
                RoleGuard(
                    viewModelFactory = viewModelFactory,
                    allowed = { RolePermissions.canEditProducts(it) },
                    onBack = { navController.popBackStack() },
                    onSessionExpired = { navController.logout() },
                ) {
                    ProductEditScreen(
                        viewModelFactory = viewModelFactory,
                        productId = id,
                        onSaved = { navController.popBackStack() },
                        onBack = { navController.popBackStack() },
                        onSessionExpired = { navController.logout() },
                    )
                }
            }
        }

        // ── Stock ─────────────────────────────────────────────────────────────
        // Остатки — все роли
        composable(AppRoutes.StockBalances) {
            StockBalanceScreen(viewModelFactory = viewModelFactory, onBack = { navController.popBackStack() })
        }

        // Низкие остатки — только ADMIN и MANAGER
        composable(AppRoutes.LowStock) {
            RoleGuard(
                viewModelFactory = viewModelFactory,
                allowed = { RolePermissions.canOpenLowStock(it) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            ) {
                LowStockScreen(viewModelFactory = viewModelFactory, onBack = { navController.popBackStack() })
            }
        }

        // Складские операции — только ADMIN и STOREKEEPER
        composable(AppRoutes.Receipt) {
            RoleGuard(
                viewModelFactory = viewModelFactory,
                allowed = { RolePermissions.canCreateStockOperations(it) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            ) {
                ReceiptScreen(
                    viewModelFactory = viewModelFactory,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.navigate(AppRoutes.StockBalances) { popUpTo(AppRoutes.Receipt) { inclusive = true } } },
                )
            }
        }
        composable(AppRoutes.Issue) {
            RoleGuard(
                viewModelFactory = viewModelFactory,
                allowed = { RolePermissions.canCreateStockOperations(it) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            ) {
                IssueScreen(
                    viewModelFactory = viewModelFactory,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.navigate(AppRoutes.StockBalances) { popUpTo(AppRoutes.Issue) { inclusive = true } } },
                )
            }
        }
        composable(AppRoutes.WriteOff) {
            RoleGuard(
                viewModelFactory = viewModelFactory,
                allowed = { RolePermissions.canCreateStockOperations(it) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            ) {
                WriteOffScreen(
                    viewModelFactory = viewModelFactory,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.navigate(AppRoutes.StockBalances) { popUpTo(AppRoutes.WriteOff) { inclusive = true } } },
                )
            }
        }
        composable(AppRoutes.Inventory) {
            RoleGuard(
                viewModelFactory = viewModelFactory,
                allowed = { RolePermissions.canCreateStockOperations(it) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            ) {
                InventoryScreen(
                    viewModelFactory = viewModelFactory,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.navigate(AppRoutes.StockBalances) { popUpTo(AppRoutes.Inventory) { inclusive = true } } },
                )
            }
        }

        // ── Operations & Reports ──────────────────────────────────────────────
        // История — все роли
        composable(AppRoutes.OperationHistory) {
            OperationHistoryScreen(viewModelFactory = viewModelFactory, onBack = { navController.popBackStack() })
        }

        // Отчёты — только ADMIN и MANAGER
        composable(AppRoutes.Reports) {
            RoleGuard(
                viewModelFactory = viewModelFactory,
                allowed = { RolePermissions.canOpenReports(it) },
                onBack = { navController.popBackStack() },
                onSessionExpired = { navController.logout() },
            ) {
                ReportsScreen(viewModelFactory = viewModelFactory, onBack = { navController.popBackStack() })
            }
        }
    }
}
