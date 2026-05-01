package com.example.warehouse_accounting_app.core.navigation

object AppRoutes {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val Dashboard = "dashboard"
    const val Profile = "profile"
    const val Users = "users"
    const val Categories = "categories"
    const val CategoryNew = "categoryNew"
    const val CategoryEdit = "categoryEdit/{id}"
    fun categoryEdit(id: Long) = "categoryEdit/$id"
    const val Products = "products"
    const val ProductDetails = "productDetails"
    const val ProductEdit = "productEdit"
    const val StockBalances = "stockBalances"
    const val Receipt = "receipt"
    const val Issue = "issue"
    const val WriteOff = "writeOff"
    const val Inventory = "inventory"
    const val OperationHistory = "operationHistory"
    const val Reports = "reports"
}
