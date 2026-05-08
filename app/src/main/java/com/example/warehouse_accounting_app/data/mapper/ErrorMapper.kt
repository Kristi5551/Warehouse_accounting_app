package com.example.warehouse_accounting_app.data.mapper

import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import java.io.IOException

private const val MSG_NETWORK = "Нет соединения с сервером"
private const val MSG_SERVER = "Ошибка сервера. Попробуйте позже"
private const val MSG_UNKNOWN = "Неизвестная ошибка"
private const val MSG_FORBIDDEN = "Недостаточно прав"
private const val MSG_NOT_FOUND = "Запись не найдена"
private const val MSG_CONFLICT = "Конфликт данных"
private const val MSG_VALIDATION = "Данные заполнены некорректно"

fun apiFailure(
    e: ApiException,
    defaultMessage: String? = null,
): AppResult.Error {
    val appError = e.toAppError(defaultMessage)
    val text =
        appError.message.ifBlank {
            defaultMessage ?: e.message?.takeIf { it.isNotBlank() } ?: MSG_UNKNOWN
        }
    return AppResult.Error(message = text, appError = appError)
}

fun networkFailure(
    e: IOException,
    defaultMessage: String? = null,
): AppResult.Error {
    val fromException =
        e.localizedMessage?.takeIf { it.isNotBlank() } ?: e.message?.takeIf { it.isNotBlank() }
    val text = fromException ?: (defaultMessage ?: MSG_NETWORK)
    val appError = AppError.Network(text)
    return AppResult.Error(message = text, appError = appError)
}

fun unknownFailure(
    throwable: Throwable,
    defaultMessage: String? = null,
): AppResult.Error {
    val primary = throwable.message?.trim()?.takeIf { it.isNotBlank() }
    val text = primary ?: (defaultMessage ?: MSG_UNKNOWN)
    val appError = AppError.Unknown(text)
    return AppResult.Error(message = text, appError = appError)
}

fun ApiException.toAppError(defaultMessage: String? = null): AppError {
    val apiMessage = this.message?.trim().orEmpty()
    val fallback =
        when {
            defaultMessage != null -> defaultMessage
            apiMessage.isNotBlank() -> apiMessage
            else -> null
        }
    return when (statusCode) {
        400 ->
            if (apiMessage.isNotBlank()) {
                AppError.Validation(apiMessage)
            } else {
                AppError.Validation(fallback ?: MSG_VALIDATION)
            }
        401 ->
            if (apiMessage.contains("inactive", ignoreCase = true) || apiMessage.contains("неактив", ignoreCase = true)) {
                AppError.SessionExpired(apiMessage.ifBlank { "Аккаунт неактивен" })
            } else {
                AppError.Unauthorized(apiMessage.ifBlank { fallback ?: "Не авторизован" })
            }
        403 -> AppError.Forbidden(apiMessage.ifBlank { fallback ?: MSG_FORBIDDEN })
        404 -> AppError.NotFound(apiMessage.ifBlank { fallback ?: MSG_NOT_FOUND })
        409 -> AppError.Conflict(apiMessage.ifBlank { fallback ?: MSG_CONFLICT })
        in 500..599 -> AppError.Server(apiMessage.ifBlank { fallback ?: MSG_SERVER })
        else -> AppError.Unknown(apiMessage.ifBlank { fallback ?: MSG_UNKNOWN })
    }
}
