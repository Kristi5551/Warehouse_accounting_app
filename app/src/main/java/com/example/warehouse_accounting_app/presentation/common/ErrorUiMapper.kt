package com.example.warehouse_accounting_app.presentation.common

import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult

private fun String?.nonBlankOr(other: String): String {
    val t = this?.trim().orEmpty()
    return if (t.isNotEmpty()) t else other
}

/**
 * Преобразует доменную ошибку в текст для отображения пользователю.
 * Если сервер уже передал сообщение — оно используется; иначе — типовой fallback.
 */
fun AppError.toUserMessage(fallback: String = ""): String {
    val primary = message.trim()
    if (primary.isNotEmpty()) return primary
    val base =
        when (this) {
            is AppError.Validation -> "Данные заполнены некорректно"
            is AppError.Unauthorized -> "Сессия истекла. Войдите снова"
            is AppError.SessionExpired -> "Сессия истекла. Войдите снова"
            is AppError.Forbidden -> "Недостаточно прав"
            is AppError.NotFound -> "Запись не найдена"
            is AppError.Conflict -> "Конфликт данных"
            is AppError.Network -> "Нет соединения с сервером"
            is AppError.Server -> "Ошибка сервера. Попробуйте позже"
            is AppError.Unknown -> fallback.nonBlankOr("Произошла ошибка")
        }
    return fallback.trim().takeIf { it.isNotEmpty() } ?: base
}

fun AppResult.Error.toUserMessage(fallback: String = ""): String {
    val fromResult = message.trim().takeIf { it.isNotEmpty() }
    if (fromResult != null) return fromResult
    return appError.toUserMessage(fallback)
}
