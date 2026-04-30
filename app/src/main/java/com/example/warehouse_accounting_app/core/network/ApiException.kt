package com.example.warehouse_accounting_app.core.network

class ApiException(
    val statusCode: Int? = null,
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
