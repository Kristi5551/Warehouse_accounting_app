package com.example.warehouse_accounting_app.di

import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import com.example.warehouse_accounting_app.core.datastore.UserPreferences
import com.example.warehouse_accounting_app.core.network.createHttpClient
import com.example.warehouse_accounting_app.data.remote.api.AuthApi
import com.example.warehouse_accounting_app.data.repository.AuthRepositoryImpl
import com.example.warehouse_accounting_app.data.repository.StubCategoryRepository
import com.example.warehouse_accounting_app.data.repository.StubProductRepository
import com.example.warehouse_accounting_app.data.repository.StubReportRepository
import com.example.warehouse_accounting_app.data.repository.StubStockRepository
import com.example.warehouse_accounting_app.data.repository.StubUserRepository
import com.example.warehouse_accounting_app.domain.repository.AuthRepository
import com.example.warehouse_accounting_app.domain.repository.CategoryRepository
import com.example.warehouse_accounting_app.domain.repository.ProductRepository
import com.example.warehouse_accounting_app.domain.repository.ReportRepository
import com.example.warehouse_accounting_app.domain.repository.StockRepository
import com.example.warehouse_accounting_app.domain.repository.UserRepository
import com.example.warehouse_accounting_app.domain.usecase.auth.LoginUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.LogoutUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.RegisterUseCase
import com.example.warehouse_accounting_app.presentation.auth.login.LoginViewModel
import com.example.warehouse_accounting_app.presentation.dashboard.DashboardViewModel
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Json { ignoreUnknownKeys = true; isLenient = true; explicitNulls = false } }
    single { AuthDataStore(androidContext()) }
    single { UserPreferences(androidContext()) }
    single { createHttpClient(get()) }
    single { AuthApi(client = get(), json = get()) }

    single<AuthRepository> { AuthRepositoryImpl(api = get(), authDataStore = get()) }
    single<UserRepository> { StubUserRepository() }
    single<CategoryRepository> { StubCategoryRepository() }
    single<ProductRepository> { StubProductRepository() }
    single<StockRepository> { StubStockRepository() }
    single<ReportRepository> { StubReportRepository() }

    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }

    viewModel { LoginViewModel(login = get()) }
    viewModel { DashboardViewModel(logout = get()) }
}
