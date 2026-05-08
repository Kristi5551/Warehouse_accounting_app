package com.example.warehouse_accounting_app.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse_accounting_app.core.util.IsoCalendarDateQuery
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.reports.LowStockReport
import com.example.warehouse_accounting_app.presentation.common.toUserMessage
import com.example.warehouse_accounting_app.domain.model.reports.OperationsReport
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

class ReportsViewModel(
    private val getStockSummaryUseCase: GetStockSummaryReportUseCase,
    private val getLowStockReportUseCase: GetLowStockReportUseCase,
    private val getOperationsReportUseCase: GetOperationsReportUseCase,
    private val getStockValueReportUseCase: GetStockValueReportUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ReportsState())
    val state = _state.asStateFlow()

    init {
        loadAll()
    }

    fun onEvent(event: ReportsEvent) {
        when (event) {
            ReportsEvent.RefreshAll -> loadAll()
            is ReportsEvent.DateFromChanged -> _state.update { it.copy(dateFromInput = event.value) }
            is ReportsEvent.DateToChanged -> _state.update { it.copy(dateToInput = event.value) }
            ReportsEvent.ApplyOperationsPeriod -> loadOperationsOnly()
        }
    }

    fun load() = onEvent(ReportsEvent.RefreshAll)

    private fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val s = _state.value
            val dateErr = IsoCalendarDateQuery.validationMessage(s.dateFromInput, s.dateToInput)
            val summaryD = async { getStockSummaryUseCase(null) }
            val lowD = async { getLowStockReportUseCase(null) }
            val valueD = async { getStockValueReportUseCase(null) }
            val opsD = async {
                if (dateErr != null) {
                    AppResult.validation(dateErr)
                } else {
                    getOperationsReportUseCase(
                        s.dateFromInput.trim().takeIf { it.isNotEmpty() },
                        s.dateToInput.trim().takeIf { it.isNotEmpty() },
                    )
                }
            }
            mergeResults(summaryD.await(), lowD.await(), opsD.await(), valueD.await())
        }
    }

    private fun loadOperationsOnly() {
        viewModelScope.launch {
            val s = _state.value
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            IsoCalendarDateQuery.validationMessage(s.dateFromInput, s.dateToInput)?.let { msg ->
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
                return@launch
            }
            when (
                val opsR =
                    getOperationsReportUseCase(
                        s.dateFromInput.trim().takeIf { it.isNotEmpty() },
                        s.dateToInput.trim().takeIf { it.isNotEmpty() },
                    )
            ) {
                is AppResult.Success ->
                    _state.update { it.copy(isLoading = false, operationsReport = opsR.data, errorMessage = null) }
                is AppResult.Error ->
                    _state.update { it.copy(isLoading = false, errorMessage = opsR.toUserMessage()) }
            }
        }
    }

    private fun mergeResults(
        summaryR: AppResult<StockSummaryReport>,
        lowR: AppResult<List<LowStockReport>>,
        opsR: AppResult<OperationsReport>,
        valueR: AppResult<StockValueReport>,
    ) {
        val summary = if (summaryR is AppResult.Success) summaryR.data else null
        val low = if (lowR is AppResult.Success) lowR.data else emptyList()
        val ops = if (opsR is AppResult.Success) opsR.data else null
        val value = if (valueR is AppResult.Success) valueR.data else null
        val firstError =
            listOfNotNull(
                (summaryR as? AppResult.Error)?.toUserMessage(),
                (lowR as? AppResult.Error)?.toUserMessage(),
                (opsR as? AppResult.Error)?.toUserMessage(),
                (valueR as? AppResult.Error)?.toUserMessage(),
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
