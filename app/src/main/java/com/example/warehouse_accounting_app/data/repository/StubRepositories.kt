package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.domain.model.Category
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.repository.CategoryRepository
import com.example.warehouse_accounting_app.domain.repository.ProductRepository
import com.example.warehouse_accounting_app.domain.repository.ReportRepository
import com.example.warehouse_accounting_app.domain.repository.StockHistoryFilter
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class StubCategoryRepository : CategoryRepository {
    override suspend fun getCategories(activeOnly: Boolean): AppResult<List<Category>> =
        AppResult.Success(emptyList())

    override suspend fun getCategoryById(id: Long): AppResult<Category> =
        AppResult.Error("Not implemented")

    override suspend fun createCategory(name: String, description: String?): AppResult<Category> =
        AppResult.Error("Not implemented")

    override suspend fun updateCategory(id: Long, name: String, description: String?, isActive: Boolean): AppResult<Category> =
        AppResult.Error("Not implemented")

    override suspend fun deleteCategory(id: Long): AppResult<Category> =
        AppResult.Error("Not implemented")
}

class StubProductRepository : ProductRepository {
    override suspend fun getProducts(search: String?, categoryId: Long?, activeOnly: Boolean): AppResult<List<Product>> =
        AppResult.Success(emptyList())

    override suspend fun getProductById(id: Long): AppResult<Product> = AppResult.Error("Not implemented")

    override suspend fun createProduct(
        article: String, name: String, categoryId: Long, unit: String,
        purchasePrice: Double, salePrice: Double, minStock: Double,
    ): AppResult<Product> = AppResult.Error("Not implemented")

    override suspend fun updateProduct(
        id: Long, article: String, name: String, categoryId: Long, unit: String,
        purchasePrice: Double, salePrice: Double, minStock: Double, isActive: Boolean,
    ): AppResult<Product> = AppResult.Error("Not implemented")

    override suspend fun deleteProduct(id: Long): AppResult<Product> = AppResult.Error("Not implemented")
}

class StubStockRepository : StockRepository {
    override suspend fun getStockBalances(warehouseId: Long?): Result<List<StockBalance>> = Result.success(emptyList())
    override suspend fun getLowStock(warehouseId: Long?): Result<List<StockBalance>> = Result.success(emptyList())
    override suspend fun createReceipt(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        price: Double,
        supplier: String?,
        comment: String?,
    ): Result<Unit> = Result.success(Unit)

    override suspend fun createIssue(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        reason: String?,
        comment: String?,
    ): Result<Unit> = Result.success(Unit)

    override suspend fun createWriteOff(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        reason: String?,
        comment: String?,
    ): Result<Unit> = Result.success(Unit)

    override suspend fun createInventory(
        warehouseId: Long,
        productId: Long,
        actualQuantity: Double,
        comment: String?,
    ): Result<Unit> = Result.success(Unit)

    override suspend fun getProductHistory(productId: Long, filter: StockHistoryFilter): Result<List<StockOperation>> =
        Result.success(emptyList())
}

class StubReportRepository : ReportRepository {
    override suspend fun getStockSummary(warehouseId: Long?): Result<List<StockSummaryReport>> = Result.success(emptyList())
    override suspend fun getLowStockReport(warehouseId: Long?): Result<List<LowStockReport>> = Result.success(emptyList())
    override suspend fun getOperationsReport(
        operationType: StockOperationType?,
        productId: Long?,
        from: String?,
        to: String?,
        userId: Long?,
    ): Result<List<OperationReport>> = Result.success(emptyList())

    override suspend fun getStockValueReport(warehouseId: Long?): Result<List<StockValueReport>> = Result.success(emptyList())
}
