package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.domain.repository.StockHistoryFilter
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class GetProductHistoryUseCase(
    private val repository: StockRepository,
) {
    suspend operator fun invoke(productId: Long, filter: StockHistoryFilter) =
        repository.getProductHistory(productId, filter)
}
