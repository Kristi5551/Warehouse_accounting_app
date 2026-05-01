package com.example.warehouse_accounting_app.core.network

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/** Понятные сообщения при сбоях сети (Ktor Android → IOException и наследники). */
fun connectivityMessage(cause: Throwable): String = when (cause) {
    is UnknownHostException ->
        "Сервер не найден по адресу. Эмулятор: 10.0.2.2. " +
            "Реальное устройство: в local.properties добавьте api.base.url=http://IP_вашего_ПК:8080"
    is ConnectException ->
        "Не удалось подключиться. Запустите сервер (gradle run или MainKt), порт 8080. " +
            "Проверьте Docker (PostgreSQL), брандмауэр и что api.base.url указывает на ваш ПК."
    is SocketTimeoutException ->
        "Сервер не ответил вовремя. Убедитесь, что Ktor запущен и не зависла БД."
    is IOException ->
        cause.message?.takeIf { it.isNotBlank() } ?: "Нет соединения с сервером"
    else ->
        cause.message?.takeIf { it.isNotBlank() } ?: "Нет соединения с сервером"
}
