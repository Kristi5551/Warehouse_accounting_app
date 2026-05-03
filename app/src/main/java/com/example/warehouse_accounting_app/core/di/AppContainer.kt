package com.example.warehouse_accounting_app.core.di

import android.content.Context
import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import com.example.warehouse_accounting_app.core.network.createHttpClient
import com.example.warehouse_accounting_app.data.remote.api.AuthApi
import com.example.warehouse_accounting_app.data.remote.api.CategoryApi
import com.example.warehouse_accounting_app.data.remote.api.ProductApi
import com.example.warehouse_accounting_app.data.remote.api.ReportApi
import com.example.warehouse_accounting_app.data.remote.api.StockApi
import com.example.warehouse_accounting_app.data.remote.api.UserApi
import com.example.warehouse_accounting_app.data.repository.AuthRepositoryImpl
import com.example.warehouse_accounting_app.data.repository.CategoryRepositoryImpl
import com.example.warehouse_accounting_app.data.repository.ProductRepositoryImpl
import com.example.warehouse_accounting_app.data.repository.ReportRepositoryImpl
import com.example.warehouse_accounting_app.data.repository.StockRepositoryImpl
import com.example.warehouse_accounting_app.data.repository.UserRepositoryImpl
import com.example.warehouse_accounting_app.domain.repository.AuthRepository
import com.example.warehouse_accounting_app.domain.repository.CategoryRepository
import com.example.warehouse_accounting_app.domain.repository.ProductRepository
import com.example.warehouse_accounting_app.domain.repository.ReportRepository
import com.example.warehouse_accounting_app.domain.repository.StockRepository
import com.example.warehouse_accounting_app.domain.repository.UserRepository
import com.example.warehouse_accounting_app.domain.usecase.auth.CheckAuthStateUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.LoginUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.LogoutUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.RegisterUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.CreateCategoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.DeleteCategoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.GetCategoriesUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.GetCategoryByIdUseCase
import com.example.warehouse_accounting_app.domain.usecase.category.UpdateCategoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.CreateProductUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.DeleteProductUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductDetailsUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.GetProductsUseCase
import com.example.warehouse_accounting_app.domain.usecase.product.UpdateProductUseCase
import com.example.warehouse_accounting_app.domain.usecase.report.GetLowStockReportUseCase
import com.example.warehouse_accounting_app.domain.usecase.report.GetOperationsReportUseCase
import com.example.warehouse_accounting_app.domain.usecase.report.GetStockSummaryReportUseCase
import com.example.warehouse_accounting_app.domain.usecase.report.GetStockValueReportUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.CreateInventoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.CreateIssueUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.CreateReceiptUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.CreateWriteOffUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.GetLowStockUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.GetOperationHistoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.GetProductHistoryUseCase
import com.example.warehouse_accounting_app.domain.usecase.stock.GetStockBalancesUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.ApproveUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.BlockUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.ChangeUserRoleUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.GetPendingUsersUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.GetUsersForOperationFiltersUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.GetUsersUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.UnblockUserUseCase
import kotlinx.serialization.json.Json

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val json: Json = Json { ignoreUnknownKeys = true; isLenient = true; explicitNulls = false }

    val authDataStore = AuthDataStore(appContext)
    private val httpClient = createHttpClient(authDataStore)
    private val authApi = AuthApi(httpClient, json)
    private val userApi = UserApi(httpClient, json)
    private val categoryApi = CategoryApi(httpClient, json)
    private val productApi = ProductApi(httpClient, json)
    private val stockApi = StockApi(httpClient, json)
    private val reportApi = ReportApi(httpClient, json)

    val authRepository: AuthRepository = AuthRepositoryImpl(authApi, authDataStore)
    val userRepository: UserRepository = UserRepositoryImpl(userApi, authDataStore)
    val categoryRepository: CategoryRepository = CategoryRepositoryImpl(categoryApi)
    val productRepository: ProductRepository = ProductRepositoryImpl(productApi)
    val stockRepository: StockRepository = StockRepositoryImpl(stockApi)
    val reportRepository: ReportRepository = ReportRepositoryImpl(reportApi)

    // Auth
    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
    val checkAuthStateUseCase = CheckAuthStateUseCase(authRepository)

    // Users
    val getUsersUseCase = GetUsersUseCase(userRepository)
    val getPendingUsersUseCase = GetPendingUsersUseCase(userRepository)
    val approveUserUseCase = ApproveUserUseCase(userRepository)
    val blockUserUseCase = BlockUserUseCase(userRepository)
    val unblockUserUseCase = UnblockUserUseCase(userRepository)
    val changeUserRoleUseCase = ChangeUserRoleUseCase(userRepository)
    val getUsersForOperationFiltersUseCase = GetUsersForOperationFiltersUseCase(userRepository)

    // Categories
    val getCategoriesUseCase = GetCategoriesUseCase(categoryRepository)
    val getCategoryByIdUseCase = GetCategoryByIdUseCase(categoryRepository)
    val createCategoryUseCase = CreateCategoryUseCase(categoryRepository)
    val updateCategoryUseCase = UpdateCategoryUseCase(categoryRepository)
    val deleteCategoryUseCase = DeleteCategoryUseCase(categoryRepository)

    // Products
    val getProductsUseCase = GetProductsUseCase(productRepository)
    val getProductDetailsUseCase = GetProductDetailsUseCase(productRepository)
    val createProductUseCase = CreateProductUseCase(productRepository)
    val updateProductUseCase = UpdateProductUseCase(productRepository)
    val deleteProductUseCase = DeleteProductUseCase(productRepository)

    // Stock
    val getStockBalancesUseCase = GetStockBalancesUseCase(stockRepository)
    val getLowStockUseCase = GetLowStockUseCase(stockRepository)
    val createReceiptUseCase = CreateReceiptUseCase(stockRepository)
    val createIssueUseCase = CreateIssueUseCase(stockRepository)
    val createWriteOffUseCase = CreateWriteOffUseCase(stockRepository)
    val createInventoryUseCase = CreateInventoryUseCase(stockRepository)
    val getOperationHistoryUseCase = GetOperationHistoryUseCase(stockRepository)
    val getProductHistoryUseCase = GetProductHistoryUseCase(stockRepository)

    // Reports
    val getStockSummaryReportUseCase = GetStockSummaryReportUseCase(reportRepository)
    val getLowStockReportUseCase = GetLowStockReportUseCase(reportRepository)
    val getOperationsReportUseCase = GetOperationsReportUseCase(reportRepository)
    val getStockValueReportUseCase = GetStockValueReportUseCase(reportRepository)
}
