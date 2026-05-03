package com.example.warehouse_accounting_app.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.warehouse_accounting_app.presentation.auth.login.LoginViewModel
import com.example.warehouse_accounting_app.presentation.auth.register.RegisterViewModel
import com.example.warehouse_accounting_app.presentation.categories.CategoryEditViewModel
import com.example.warehouse_accounting_app.presentation.categories.CategoryListViewModel
import com.example.warehouse_accounting_app.presentation.dashboard.DashboardViewModel
import com.example.warehouse_accounting_app.presentation.operations.OperationHistoryViewModel
import com.example.warehouse_accounting_app.presentation.products.ProductDetailsViewModel
import com.example.warehouse_accounting_app.presentation.products.ProductEditViewModel
import com.example.warehouse_accounting_app.presentation.products.ProductListViewModel
import com.example.warehouse_accounting_app.presentation.profile.ProfileViewModel
import com.example.warehouse_accounting_app.presentation.reports.ReportsViewModel
import com.example.warehouse_accounting_app.presentation.splash.SplashViewModel
import com.example.warehouse_accounting_app.presentation.stock.inventory.InventoryViewModel
import com.example.warehouse_accounting_app.presentation.stock.issue.IssueViewModel
import com.example.warehouse_accounting_app.presentation.stock.balances.LowStockViewModel
import com.example.warehouse_accounting_app.presentation.stock.balances.StockBalanceViewModel
import com.example.warehouse_accounting_app.presentation.stock.receipt.ReceiptViewModel
import com.example.warehouse_accounting_app.presentation.stock.writeoff.WriteOffViewModel
import com.example.warehouse_accounting_app.core.navigation.RouteGuardViewModel
import com.example.warehouse_accounting_app.presentation.users.UserListViewModel

class WarehouseViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val c = container
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(c.checkAuthStateUseCase) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(c.loginUseCase) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(c.registerUseCase) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(c.logoutUseCase, c.getCurrentUserUseCase) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(c.getCurrentUserUseCase, c.logoutUseCase) as T
            modelClass.isAssignableFrom(UserListViewModel::class.java) ->
                UserListViewModel(
                    c.getUsersUseCase,
                    c.getPendingUsersUseCase,
                    c.approveUserUseCase,
                    c.blockUserUseCase,
                    c.unblockUserUseCase,
                    c.changeUserRoleUseCase,
                    c.createAdminUserUseCase,
                    c.getCurrentUserUseCase,
                ) as T
            modelClass.isAssignableFrom(CategoryListViewModel::class.java) -> CategoryListViewModel(c.getCategoriesUseCase, c.deleteCategoryUseCase, c.getCurrentUserUseCase) as T
            modelClass.isAssignableFrom(CategoryEditViewModel::class.java) -> CategoryEditViewModel(c.getCategoryByIdUseCase, c.createCategoryUseCase, c.updateCategoryUseCase) as T
            modelClass.isAssignableFrom(ProductListViewModel::class.java) -> ProductListViewModel(c.getProductsUseCase, c.deleteProductUseCase, c.getCurrentUserUseCase, c.getCategoriesUseCase) as T
            modelClass.isAssignableFrom(ProductDetailsViewModel::class.java) ->
                ProductDetailsViewModel(c.getProductDetailsUseCase, c.getProductHistoryUseCase, c.getCurrentUserUseCase) as T
            modelClass.isAssignableFrom(ProductEditViewModel::class.java) -> ProductEditViewModel(c.getProductDetailsUseCase, c.createProductUseCase, c.updateProductUseCase, c.getCategoriesUseCase) as T
            modelClass.isAssignableFrom(StockBalanceViewModel::class.java) -> StockBalanceViewModel(c.getStockBalancesUseCase) as T
            modelClass.isAssignableFrom(LowStockViewModel::class.java) -> LowStockViewModel(c.getLowStockUseCase) as T
            modelClass.isAssignableFrom(ReceiptViewModel::class.java) -> ReceiptViewModel(c.createReceiptUseCase, c.getProductsUseCase) as T
            modelClass.isAssignableFrom(IssueViewModel::class.java) -> IssueViewModel(c.createIssueUseCase, c.getProductsUseCase) as T
            modelClass.isAssignableFrom(WriteOffViewModel::class.java) -> WriteOffViewModel(c.createWriteOffUseCase, c.getProductsUseCase) as T
            modelClass.isAssignableFrom(InventoryViewModel::class.java) ->
                InventoryViewModel(c.createInventoryUseCase, c.getProductsUseCase, c.getStockBalancesUseCase) as T
            modelClass.isAssignableFrom(OperationHistoryViewModel::class.java) ->
                OperationHistoryViewModel(
                    c.getOperationHistoryUseCase,
                    c.getProductsUseCase,
                    c.getUsersForOperationFiltersUseCase,
                ) as T
            modelClass.isAssignableFrom(ReportsViewModel::class.java) -> ReportsViewModel(c.getStockSummaryReportUseCase, c.getLowStockReportUseCase, c.getOperationsReportUseCase, c.getStockValueReportUseCase) as T
            modelClass.isAssignableFrom(RouteGuardViewModel::class.java) -> RouteGuardViewModel(c.getCurrentUserUseCase) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
