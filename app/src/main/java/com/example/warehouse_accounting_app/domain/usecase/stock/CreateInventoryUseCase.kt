package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class CreateInventoryUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(
        warehouseId: Long, productId: Long, actualQuantity: Double, comment: String?,
    ): AppResult<StockOperation> = repository.createInventory(warehouseId, productId, actualQuantity, comment)
}
