package com.example.warehouse_accounting_app.domain.usecase.user

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.UserPick
import com.example.warehouse_accounting_app.domain.repository.UserRepository

class GetUsersForOperationFiltersUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): AppResult<List<UserPick>> = repository.getUsersForOperationFilters()
}
