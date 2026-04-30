package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.domain.repository.StockRepository

class GetStockBalancesUseCase(
    private val repository: StockRepository,
) {
    suspend operator fun invoke(warehouseId: Long?) = repository.getStockBalances(warehouseId)
}
