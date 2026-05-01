package com.example.warehouse_accounting_app.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.domain.model.reports.OperationReport
import com.example.warehouse_accounting_app.domain.model.reports.StockSummaryReport
import com.example.warehouse_accounting_app.domain.model.reports.StockValueReport
import com.example.warehouse_accounting_app.domain.usecase.report.GetLowStockReportUseCase
import com.example.warehouse_accounting_app.domain.usecase.report.GetOperationsReportUseCase
import com.example.warehouse_accounting_app.domain.usecase.report.GetStockSummaryReportUseCase
import com.example.warehouse_accounting_app.domain.usecase.report.GetStockValueReportUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportsState(
    val isLoading: Boolean = false,
    val stockSummary: List<StockSummaryReport> = emptyList(),
    val lowStockReport: List<LowStockReport> = emptyList(),
    val operationsReport: List<OperationReport> = emptyList(),
    val stockValueReport: List<StockValueReport> = emptyList(),
    val errorMessage: String? = null,
)

class ReportsViewModel(
    private val getStockSummaryUseCase: GetStockSummaryReportUseCase,
    private val getLowStockReportUseCase: GetLowStockReportUseCase,
    private val getOperationsReportUseCase: GetOperationsReportUseCase,
    private val getStockValueReportUseCase: GetStockValueReportUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ReportsState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val summaryResult = async { getStockSummaryUseCase(null) }.await()
            val lowResult = async { getLowStockReportUseCase(null) }.await()
            val opsResult = async { getOperationsReportUseCase(null, null, null, null, null) }.await()
            val valueResult = async { getStockValueReportUseCase(null) }.await()

            val summary: List<StockSummaryReport> = if (summaryResult is AppResult.Success) summaryResult.data else emptyList()
            val low: List<LowStockReport> = if (lowResult is AppResult.Success) lowResult.data else emptyList()
            val ops: List<OperationReport> = if (opsResult is AppResult.Success) opsResult.data else emptyList()
            val value: List<StockValueReport> = if (valueResult is AppResult.Success) valueResult.data else emptyList()

            val firstError = listOfNotNull(
                (summaryResult as? AppResult.Error)?.message,
                (lowResult as? AppResult.Error)?.message,
                (opsResult as? AppResult.Error)?.message,
                (valueResult as? AppResult.Error)?.message,
            ).firstOrNull()

            _state.update {
                it.copy(
                    isLoading = false,
                    stockSummary = summary,
                    lowStockReport = low,
                    operationsReport = ops,
                    stockValueReport = value,
                    errorMessage = firstError,
                )
            }
        }
    }
}
