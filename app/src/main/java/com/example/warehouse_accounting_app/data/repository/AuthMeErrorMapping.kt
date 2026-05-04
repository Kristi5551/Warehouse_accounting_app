package com.example.warehouse_accounting_app.data.repository

import java.util.Locale

/**
 * Тексты 403 от [AccessControlService.requireActiveUser] и аналоги / возможные сообщения сервера —
 * признак того, что учётную запись дальше с этим токеном использовать нельзя → очищаем сессию.
 */
internal fun String?.meansAccountNoLongerActiveForSession(): Boolean {
    val m = this?.lowercase(Locale.ROOT) ?: return false
    return m.contains("заблокирован") ||
        m.contains("ожидает подтверждения") ||
        m.contains("не активен") ||
        m.contains("неактивен") ||
        m.contains("аккаунт не актив") ||
        m.contains("пользователь больше не активен")
}
