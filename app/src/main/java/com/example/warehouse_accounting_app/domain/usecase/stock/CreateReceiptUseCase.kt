package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class CreateReceiptUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(
        warehouseId: Long,
        productId: Long,
        quantity: Double,
        price: Double,
        supplier: String?,
        comment: String?,
    ): AppResult<StockOperation> = repository.createReceipt(warehouseId, productId, quantity, price, supplier, comment)
}
