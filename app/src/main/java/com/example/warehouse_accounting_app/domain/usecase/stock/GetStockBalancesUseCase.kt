package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockBalance
import com.example.warehouse_accounting_app.domain.model.StockStatus
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class GetStockBalancesUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(
        search: String? = null,
        categoryId: Long? = null,
        status: StockStatus? = null,
    ): AppResult<List<StockBalance>> =
        repository.getStockBalances(search, categoryId, status)
}
