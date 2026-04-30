package com.example.warehouse_accounting_app.core.di

import android.content.Context
import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import com.example.warehouse_accounting_app.core.network.createHttpClient
import com.example.warehouse_accounting_app.data.remote.api.AuthApi
import com.example.warehouse_accounting_app.data.repository.AuthRepositoryImpl
import com.example.warehouse_accounting_app.domain.repository.AuthRepository
import com.example.warehouse_accounting_app.domain.usecase.auth.CheckAuthStateUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.LoginUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.LogoutUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.RegisterUseCase
import kotlinx.serialization.json.Json

class AppContainer(
    context: Context,
) {
    private val appContext = context.applicationContext

    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    val authDataStore: AuthDataStore = AuthDataStore(appContext)
    private val httpClient = createHttpClient(authDataStore)
    private val authApi = AuthApi(httpClient, json)

    val authRepository: AuthRepository = AuthRepositoryImpl(authApi, authDataStore)

    val loginUseCase: LoginUseCase = LoginUseCase(authRepository)
    val registerUseCase: RegisterUseCase = RegisterUseCase(authRepository)
    val logoutUseCase: LogoutUseCase = LogoutUseCase(authRepository)
    val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
    val checkAuthStateUseCase: CheckAuthStateUseCase = CheckAuthStateUseCase(authRepository)
}
