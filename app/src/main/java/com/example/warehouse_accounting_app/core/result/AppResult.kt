package com.example.warehouse_accounting_app.core.result

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()

    /**
     * @param message   Человекочитаемое описание ошибки (для UI).
     * @param throwable Исходное исключение (опционально, только для логов).
     * @param appError  Типизированная доменная причина ошибки. Не null если
     *                  слой data смог классифицировать ошибку.
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null,
        val appError: AppError? = null,
    ) : AppResult<Nothing>()
}
