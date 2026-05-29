package com.example.warehouse_accounting_app.testutil

import com.example.warehouse_accounting_app.domain.model.Product
import com.example.warehouse_accounting_app.domain.model.StockOperation
import com.example.warehouse_accounting_app.domain.model.StockOperationType
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.model.UserStatus

fun sampleUser(
    id: Long = 1L,
    email: String = "keeper@warehouse.local",
    fullName: String = "Иван Кладовщик",
    role: UserRole = UserRole.STOREKEEPER,
    status: UserStatus = UserStatus.ACTIVE,
): User = User(
    id = id,
    email = email,
    fullName = fullName,
    role = role,
    status = status,
)

fun sampleProduct(
    id: Long = 10L,
    article: String = "SKU-001",
    name: String = "Болт М8",
): Product = Product(
    id = id,
    article = article,
    name = name,
    categoryId = 1L,
    categoryName = "Крепёж",
    unit = "шт",
    purchasePrice = 5.0,
    salePrice = 8.0,
    minStock = 10.0,
    isActive = true,
    createdAt = "2026-01-01T00:00:00",
    updatedAt = "2026-01-01T00:00:00",
)

fun sampleReceiptOperation(id: Long = 100L): StockOperation = StockOperation(
    id = id,
    operationType = StockOperationType.RECEIPT,
    warehouseId = 1L,
    warehouseName = "Основной склад",
    createdBy = 1L,
    createdByName = "Иван Кладовщик",
    createdAt = "2026-05-29T10:00:00",
    comment = null,
    items = emptyList(),
)
