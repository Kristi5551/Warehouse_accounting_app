package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class CreateIssueUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(
        warehouseId: Long, productId: Long, quantity: Double, reason: String?, comment: String?,
    ): AppResult<StockOperation> = repository.createIssue(warehouseId, productId, quantity, reason, comment)
}
