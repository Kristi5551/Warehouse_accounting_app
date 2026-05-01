package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class GetLowStockUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(warehouseId: Long?): AppResult<List<StockBalance>> =
        repository.getLowStock(warehouseId)
}
