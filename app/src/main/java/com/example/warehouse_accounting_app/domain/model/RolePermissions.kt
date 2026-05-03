package com.example.warehouse_accounting_app.domain.model

/**
 * Единые правила доступа по ролям.
 * Используется в Dashboard, route guard и экранах для показа/скрытия кнопок.
 *
 * Правило: сервер всегда является последней линией защиты (возвращает 403).
 * Android-слой только улучшает UX, скрывая недоступные элементы заранее.
 */
object RolePermissions {

    // ── Пользователи ──────────────────────────────────────────────────────────

    /** Экран управления пользователями — только ADMIN. */
    fun canOpenUsers(role: UserRole): Boolean = role == UserRole.ADMIN

    // ── Категории ─────────────────────────────────────────────────────────────

    /** Просмотр списка категорий — все роли. */
    fun canOpenCategories(role: UserRole): Boolean = true

    /** Создание, редактирование и деактивация категорий — только ADMIN. */
    fun canEditCategories(role: UserRole): Boolean = role == UserRole.ADMIN

    // ── Товары ────────────────────────────────────────────────────────────────

    /** Просмотр списка товаров — все роли. */
    fun canOpenProducts(role: UserRole): Boolean = true

    /** Создание, редактирование и деактивация товаров — только ADMIN. */
    fun canEditProducts(role: UserRole): Boolean = role == UserRole.ADMIN

    // ── Склад ─────────────────────────────────────────────────────────────────

    /** Общие остатки — все роли. */
    fun canOpenStockBalances(role: UserRole): Boolean = true

    /** Раздел «Низкие остатки» — аналитика для ADMIN и MANAGER. */
    fun canOpenLowStock(role: UserRole): Boolean =
        role == UserRole.ADMIN || role == UserRole.MANAGER

    /** Складские операции (приход/расход/списание/инвентаризация) — ADMIN и STOREKEEPER. */
    fun canCreateStockOperations(role: UserRole): Boolean =
        role == UserRole.ADMIN || role == UserRole.STOREKEEPER

    // ── История и отчёты ──────────────────────────────────────────────────────

    /** История операций — все роли. */
    fun canOpenOperationHistory(role: UserRole): Boolean = true

    /** Отчёты — аналитика для ADMIN и MANAGER. */
    fun canOpenReports(role: UserRole): Boolean =
        role == UserRole.ADMIN || role == UserRole.MANAGER

    // ── Профиль ───────────────────────────────────────────────────────────────

    /** Профиль — все роли. */
    fun canOpenProfile(role: UserRole): Boolean = true
}
