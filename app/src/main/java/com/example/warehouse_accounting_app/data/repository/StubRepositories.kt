package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.Category
import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.StockStatus
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport
import com.example.warehouse_accounting_app.domain.repository.CategoryRepository
import com.example.warehouse_accounting_app.domain.repository.OperationsFilter
import com.example.warehouse_accounting_app.domain.repository.ProductRepository
import com.example.warehouse_accounting_app.domain.repository.ReportRepository
import com.example.warehouse_accounting_app.domain.repository.StockHistoryFilter
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class StubCategoryRepository : CategoryRepository {
    override suspend fun getCategories(activeOnly: Boolean): AppResult<List<Category>> = AppResult.Success(emptyList())
    override suspend fun getCategoryById(id: Long): AppResult<Category> = AppResult.Error("Not implemented")
    override suspend fun createCategory(name: String, description: String?): AppResult<Category> = AppResult.Error("Not implemented")
    override suspend fun updateCategory(id: Long, name: String, description: String?, isActive: Boolean): AppResult<Category> = AppResult.Error("Not implemented")
    override suspend fun deleteCategory(id: Long): AppResult<Category> = AppResult.Error("Not implemented")
}

class StubProductRepository : ProductRepository {
    override suspend fun getProducts(search: String?, categoryId: Long?, activeOnly: Boolean): AppResult<List<Product>> = AppResult.Success(emptyList())
    override suspend fun getProductById(id: Long): AppResult<Product> = AppResult.Error("Not implemented")
    override suspend fun createProduct(article: String, name: String, categoryId: Long, unit: String, purchasePrice: Double, salePrice: Double, minStock: Double): AppResult<Product> = AppResult.Error("Not implemented")
    override suspend fun updateProduct(id: Long, article: String, name: String, categoryId: Long, unit: String, purchasePrice: Double, salePrice: Double, minStock: Double, isActive: Boolean): AppResult<Product> = AppResult.Error("Not implemented")
    override suspend fun deleteProduct(id: Long): AppResult<Product> = AppResult.Error("Not implemented")
}

class StubStockRepository : StockRepository {
    override suspend fun getStockBalances(search: String?, categoryId: Long?, status: StockStatus?): AppResult<List<StockBalance>> = AppResult.Success(emptyList())
    override suspend fun getLowStock(): AppResult<List<StockBalance>> = AppResult.Success(emptyList())
    override suspend fun createReceipt(warehouseId: Long, productId: Long, quantity: Double, price: Double, supplier: String?, comment: String?): AppResult<StockOperation> = AppResult.Error("Not implemented")
    override suspend fun createIssue(warehouseId: Long, productId: Long, quantity: Double, reason: String?, comment: String?): AppResult<StockOperation> = AppResult.Error("Not implemented")
    override suspend fun createWriteOff(warehouseId: Long, productId: Long, quantity: Double, reason: String?, comment: String?): AppResult<StockOperation> = AppResult.Error("Not implemented")
    override suspend fun createInventory(warehouseId: Long, productId: Long, actualQuantity: Double, comment: String?): AppResult<StockOperation> = AppResult.Error("Not implemented")
    override suspend fun getOperations(filter: OperationsFilter): AppResult<List<StockOperation>> =
        AppResult.Success(emptyList())

    override suspend fun getProductHistory(productId: Long, filter: StockHistoryFilter): AppResult<List<StockOperation>> =
        AppResult.Success(emptyList())
}

class StubReportRepository : ReportRepository {
    override suspend fun getStockSummary(warehouseId: Long?): AppResult<List<StockSummaryReport>> = AppResult.Success(emptyList())
    override suspend fun getLowStockReport(warehouseId: Long?): AppResult<List<LowStockReport>> = AppResult.Success(emptyList())
    override suspend fun getOperationsReport(operationType: StockOperationType?, productId: Long?, from: String?, to: String?, userId: Long?): AppResult<List<OperationReport>> = AppResult.Success(emptyList())
    override suspend fun getStockValueReport(warehouseId: Long?): AppResult<List<StockValueReport>> = AppResult.Success(emptyList())
}
