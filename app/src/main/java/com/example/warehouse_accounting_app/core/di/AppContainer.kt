package com.example.warehouse_accounting_app.core.di

import android.content.Context
import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import com.example.warehouse_accounting_app.core.network.createHttpClient
import com.example.warehouse_accounting_app.data.remote.api.AuthApi
import com.example.warehouse_accounting_app.data.remote.api.UserApi
import com.example.warehouse_accounting_app.data.repository.AuthRepositoryImpl
import com.example.warehouse_accounting_app.data.repository.UserRepositoryImpl
import com.example.warehouse_accounting_app.domain.repository.AuthRepository
import com.example.warehouse_accounting_app.domain.repository.UserRepository
import com.example.warehouse_accounting_app.domain.usecase.auth.CheckAuthStateUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.LoginUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.LogoutUseCase
import com.example.warehouse_accounting_app.domain.usecase.auth.RegisterUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.ApproveUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.BlockUserUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.ChangeUserRoleUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.GetPendingUsersUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.GetUsersUseCase
import com.example.warehouse_accounting_app.domain.usecase.user.UnblockUserUseCase
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
    private val userApi = UserApi(httpClient, json)

    val authRepository: AuthRepository = AuthRepositoryImpl(authApi, authDataStore)
    val userRepository: UserRepository = UserRepositoryImpl(userApi, authDataStore)

    val loginUseCase: LoginUseCase = LoginUseCase(authRepository)
    val registerUseCase: RegisterUseCase = RegisterUseCase(authRepository)
    val logoutUseCase: LogoutUseCase = LogoutUseCase(authRepository)
    val getCurrentUserUseCase: GetCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
    val checkAuthStateUseCase: CheckAuthStateUseCase = CheckAuthStateUseCase(authRepository)

    val getUsersUseCase: GetUsersUseCase = GetUsersUseCase(userRepository)
    val getPendingUsersUseCase: GetPendingUsersUseCase = GetPendingUsersUseCase(userRepository)
    val approveUserUseCase: ApproveUserUseCase = ApproveUserUseCase(userRepository)
    val blockUserUseCase: BlockUserUseCase = BlockUserUseCase(userRepository)
    val unblockUserUseCase: UnblockUserUseCase = UnblockUserUseCase(userRepository)
    val changeUserRoleUseCase: ChangeUserRoleUseCase = ChangeUserRoleUseCase(userRepository)
}
