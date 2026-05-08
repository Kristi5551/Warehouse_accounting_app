package com.example.warehouse_accounting_app.presentation.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.warehouse_accounting_app.core.navigation.AppRoutes
import com.example.warehouse_accounting_app.domain.model.RolePermissions
import com.example.warehouse_accounting_app.domain.model.UserRole

internal data class DashboardSection(
    val route: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
)

private data class DashboardSectionDefinition(
    val route: String,
    val titleFor: (UserRole) -> String,
    val descriptionFor: (UserRole) -> String,
    val icon: ImageVector,
    val canOpen: (UserRole) -> Boolean,
)

private val dashboardSectionCatalog: List<DashboardSectionDefinition> = listOf(
    DashboardSectionDefinition(
        route = AppRoutes.Users,
        titleFor = { "Пользователи" },
        descriptionFor = { "Подтверждение сотрудников, роли и блокировка аккаунтов" },
        icon = Icons.Filled.Group,
        canOpen = RolePermissions::canOpenUsers,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.Categories,
        titleFor = { "Категории" },
        descriptionFor = { role ->
            when (role) {
                UserRole.ADMIN -> "Группы товаров для удобного учёта"
                else -> "Группы товаров"
            }
        },
        icon = Icons.Filled.Category,
        canOpen = RolePermissions::canOpenCategories,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.Products,
        titleFor = { "Товары" },
        descriptionFor = { role ->
            when (role) {
                UserRole.ADMIN -> "Каталог товаров торговой компании"
                else -> "Каталог товаров"
            }
        },
        icon = Icons.Filled.Inventory,
        canOpen = RolePermissions::canOpenProducts,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.StockBalances,
        titleFor = { "Остатки" },
        descriptionFor = { "Текущее количество товаров на складе" },
        icon = Icons.Filled.Warehouse,
        canOpen = RolePermissions::canOpenStockBalances,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.LowStock,
        titleFor = { "Низкие остатки" },
        descriptionFor = { role ->
            when (role) {
                UserRole.MANAGER -> "Товары для пополнения"
                else -> "Товары, которые нужно пополнить"
            }
        },
        icon = Icons.AutoMirrored.Filled.TrendingDown,
        canOpen = RolePermissions::canOpenLowStock,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.Receipt,
        titleFor = { "Приход" },
        descriptionFor = { "Поступление товаров на склад" },
        icon = Icons.Filled.MoveToInbox,
        canOpen = RolePermissions::canCreateStockOperations,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.Issue,
        titleFor = { "Расход" },
        descriptionFor = { "Выдача или продажа товаров" },
        icon = Icons.Filled.Output,
        canOpen = RolePermissions::canCreateStockOperations,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.WriteOff,
        titleFor = { "Списание" },
        descriptionFor = { role ->
            when (role) {
                UserRole.ADMIN -> "Учёт брака, потерь и повреждений"
                else -> "Учёт брака и потерь"
            }
        },
        icon = Icons.Filled.DeleteForever,
        canOpen = RolePermissions::canCreateStockOperations,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.Inventory,
        titleFor = { "Инвентаризация" },
        descriptionFor = { role ->
            when (role) {
                UserRole.ADMIN -> "Сверка учётного и фактического количества"
                else -> "Сверка остатков"
            }
        },
        icon = Icons.AutoMirrored.Filled.FactCheck,
        canOpen = RolePermissions::canCreateStockOperations,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.OperationHistory,
        titleFor = { "История" },
        descriptionFor = { "Все движения товаров на складе" },
        icon = Icons.Filled.History,
        canOpen = RolePermissions::canOpenOperationHistory,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.Reports,
        titleFor = { "Отчёты" },
        descriptionFor = { role ->
            when (role) {
                UserRole.MANAGER -> "Аналитика по складу"
                else -> "Аналитика по остаткам и движениям товаров"
            }
        },
        icon = Icons.Filled.BarChart,
        canOpen = RolePermissions::canOpenReports,
    ),
    DashboardSectionDefinition(
        route = AppRoutes.Profile,
        titleFor = { "Профиль" },
        descriptionFor = { role ->
            when (role) {
                UserRole.ADMIN -> "Данные учётной записи и выход"
                else -> "Данные учётной записи"
            }
        },
        icon = Icons.Filled.Person,
        canOpen = RolePermissions::canOpenProfile,
    ),
)

internal fun buildDashboardSections(role: UserRole): List<DashboardSection> =
    dashboardSectionCatalog
        .filter { it.canOpen(role) }
        .map { def ->
            DashboardSection(
                route = def.route,
                title = def.titleFor(role),
                description = def.descriptionFor(role),
                icon = def.icon,
            )
        }
