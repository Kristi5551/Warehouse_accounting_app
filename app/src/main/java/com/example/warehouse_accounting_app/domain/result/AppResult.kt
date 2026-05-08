package com.example.warehouse_accounting_app.domain.result

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()

    data class Error(
        val message: String,
        val appError: AppError,
    ) : AppResult<Nothing>() {
        init {
            require(message.isNotEmpty() || appError.message.isNotEmpty()) {
                "AppResult.Error requires non-empty message or appError.message"
            }
        }
    }

    companion object {
        fun validation(message: String): Error =
            Error(message, AppError.Validation(message))
    }
}
