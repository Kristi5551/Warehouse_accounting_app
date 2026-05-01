package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.repository.StockHistoryFilter
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class GetProductHistoryUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(productId: Long, filter: StockHistoryFilter): AppResult<List<StockOperation>> =
        repository.getProductHistory(productId, filter)
}
