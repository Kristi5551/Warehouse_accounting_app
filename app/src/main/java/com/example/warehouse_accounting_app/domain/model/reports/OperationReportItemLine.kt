package com.example.warehouse_accounting_app.domain.model.reports

data class OperationReportItemLine(
    val productArticle: String,
    val productName: String,
    val quantity: Double,
    val price: Double?,
)
