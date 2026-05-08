package com.example.warehouse_accounting_app.domain.result

sealed class AppError(open val message: String) {
    data class Validation(override val message: String) : AppError(message)

    data class Unauthorized(override val message: String) : AppError(message)

    data class SessionExpired(override val message: String) : AppError(message)

    data class Forbidden(override val message: String) : AppError(message)

    data class NotFound(override val message: String) : AppError(message)

    data class Conflict(override val message: String) : AppError(message)

    data class Network(override val message: String) : AppError(message)

    data class Server(override val message: String) : AppError(message)

    data class Unknown(override val message: String) : AppError(message)
}
