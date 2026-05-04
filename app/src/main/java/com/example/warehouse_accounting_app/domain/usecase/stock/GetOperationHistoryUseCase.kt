package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.repository.OperationsFilter
import com.example.warehouse_accounting_app.domain.repository.StockRepository

class GetOperationHistoryUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(filter: OperationsFilter): AppResult<List<StockOperation>> =
        repository.getOperations(filter)
}
