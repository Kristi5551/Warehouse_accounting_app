package com.example.warehouse_accounting_app.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.usecase.auth.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportsAccessViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    sealed interface AccessState {
        data object Loading : AccessState
        data object Denied : AccessState
        data object Allowed : AccessState
    }

    private val _access = MutableStateFlow<AccessState>(AccessState.Loading)
    val access: StateFlow<AccessState> = _access.asStateFlow()

    init {
        viewModelScope.launch {
            _access.value =
                when (val r = getCurrentUserUseCase()) {
                    is AppResult.Success -> {
                        when (r.data.role) {
                            UserRole.ADMIN, UserRole.MANAGER -> AccessState.Allowed
                            UserRole.STOREKEEPER -> AccessState.Denied
                        }
                    }
                    else -> AccessState.Denied
                }
        }
    }
}
