package com.example.warehouse_accounting_app.testutil

import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.StockStatus
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserPick
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.AuthRepository
import com.example.warehouse_accounting_app.domain.repository.OperationsFilter
import com.example.warehouse_accounting_app.domain.repository.ProductRepository
import com.example.warehouse_accounting_app.domain.repository.StockHistoryFilter
import com.example.warehouse_accounting_app.domain.repository.StockRepository
import com.example.warehouse_accounting_app.domain.repository.UserRepository
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAuthRepository(
    private var token: String? = null,
    private val loginResult: AppResult<User> = AppResult.Success(sampleUser()),
    private val registerResult: AppResult<User> = AppResult.Success(sampleUser()),
    private val currentUserResult: AppResult<User> = AppResult.Success(sampleUser()),
) : AuthRepository {
    var lastLoginEmail: String? = null
    var lastLoginPassword: String? = null
    var lastRegisterEmail: String? = null
    var logoutCalled = false

    fun setToken(value: String?) {
        token = value
    }

    override suspend fun login(email: String, password: String): AppResult<User> {
        lastLoginEmail = email
        lastLoginPassword = password
        return loginResult
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
        requestedRole: UserRole,
    ): AppResult<User> {
        lastRegisterEmail = email
        return registerResult
    }

    override suspend fun getCurrentUser(): AppResult<User> = currentUserResult

    override suspend fun logout() {
        logoutCalled = true
        token = null
    }

    override fun observeToken(): Flow<String?> = flowOf(token)

    override suspend fun getTokenOnce(): String? = token
}

class FakeUserRepository(
    private val approveResult: AppResult<User> = AppResult.Success(sampleUser()),
) : UserRepository {
    var lastApprovedId: Long? = null

    override suspend fun getUsers(): AppResult<List<User>> = AppResult.Success(emptyList())

    override suspend fun getPendingUsers(): AppResult<List<User>> = AppResult.Success(emptyList())

    override suspend fun approveUser(id: Long): AppResult<User> {
        lastApprovedId = id
        return approveResult
    }

    override suspend fun blockUser(id: Long): AppResult<User> = AppResult.Success(sampleUser())

    override suspend fun unblockUser(id: Long): AppResult<User> = AppResult.Success(sampleUser())

    override suspend fun changeUserRole(id: Long, role: UserRole): AppResult<User> =
        AppResult.Success(sampleUser(role = role))

    override suspend fun createAdmin(fullName: String, email: String, password: String): AppResult<User> =
        AppResult.Success(sampleUser(role = UserRole.ADMIN))

    override suspend fun getUsersForOperationFilters(): AppResult<List<UserPick>> = AppResult.Success(emptyList())
}

class FakeProductRepository(
    private val products: List<Product> = listOf(sampleProduct()),
) : ProductRepository {
    var lastActiveOnly: Boolean? = null

    override suspend fun getProducts(search: String?, categoryId: Long?, activeOnly: Boolean): AppResult<List<Product>> {
        lastActiveOnly = activeOnly
        return AppResult.Success(products)
    }

    override suspend fun getProductById(id: Long): AppResult<Product> =
        products.firstOrNull { it.id == id }?.let { AppResult.Success(it) }
            ?: AppResult.Error("Товар не найден", AppError.NotFound("Товар не найден"))

    override suspend fun createProduct(
        article: String,
        name: String,
        categoryId: Long,
        unit: String,
        purchasePrice: Double,
        salePrice: Double,
        minStock: Double,
    ): AppResult<Product> = AppResult.Success(sampleProduct())

    override suspend fun updateProduct(
        id: Long,
        article: String,
        name: String,
        categoryId: Long,
        unit: String,
        purchasePrice: Double,
        salePrice: Double,
        minStock: Double,
        isActive: Boolean,
    ): AppResult<Product> = AppResult.Success(sampleProduct(id = id))

    override suspend fun deleteProduct(id: Long): AppResult<Product> = AppResult.Success(sampleProduct(id = id))
}

class FakeStockRepository(
    private val receiptResult: AppResult<StockOperation> = AppResult.Success(sampleReceiptOperation()),
) : StockRepository {
    var lastReceipt: ReceiptCall? = null

    data class ReceiptCall(
        val warehouseId: Long,
        val productId: Long,
        val quantity: Double,
        val price: Double,
        val supplier: String?,
        val comment: String?,
    )

    override suspend fun getStockBalances(
        search: String?,
        categoryId: Long?,
        status: StockStatus?,
    ): AppResult<List<StockBalance>> = AppResult.Success(emptyList())

    override suspend fun getLowStock(): AppResult<List<StockBalance>> = AppResult.Success(emptyList())

    override suspend fun createReceipt(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        price: Double,
        supplier: String?,
        comment: String?,
    ): AppResult<StockOperation> {
        lastReceipt = ReceiptCall(warehouseId, productId, quantity, price, supplier, comment)
        return receiptResult
    }

    override suspend fun createIssue(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        reason: String?,
        comment: String?,
    ): AppResult<StockOperation> = AppResult.Success(sampleReceiptOperation())

    override suspend fun createWriteOff(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        reason: String?,
        comment: String?,
    ): AppResult<StockOperation> = AppResult.Success(sampleReceiptOperation())

    override suspend fun createInventory(
        warehouseId: Long,
        productId: Long,
        actualQuantity: Double,
        comment: String?,
    ): AppResult<StockOperation> = AppResult.Success(sampleReceiptOperation())

    override suspend fun getOperations(filter: OperationsFilter): AppResult<List<StockOperation>> =
        AppResult.Success(emptyList())

    override suspend fun getProductHistory(productId: Long, filter: StockHistoryFilter): AppResult<List<StockOperation>> =
        AppResult.Success(emptyList())
}
