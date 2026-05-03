package com.example.warehouse_accounting_app.domain.model.reports

data class OperationsReport(
    val operations: List<OperationReportLine>,
    val receiptCount: Int,
    val issueCount: Int,
    val writeOffCount: Int,
    val inventoryCount: Int,
)
