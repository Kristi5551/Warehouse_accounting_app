package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class CreateWriteOffUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(
        warehouseId: Long, productId: Long, quantity: Double, reason: String?, comment: String?,
    ): AppResult<Unit> = repository.createWriteOff(warehouseId, productId, quantity, reason, comment)
}
