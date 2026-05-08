package com.example.warehouse_accounting_app.presentation.dashboard

import com.example.warehouse_accounting_app.core.navigation.AppRoutes
import com.example.warehouse_accounting_app.domain.model.RolePermissions
import com.example.warehouse_accounting_app.domain.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Test

class RoleAccessContractTest {

    
    private val expectedRoutesByRole: Map<UserRole, Set<String>> =
        mapOf(
            UserRole.ADMIN to
                setOf(
                    AppRoutes.Users,
                    AppRoutes.Categories,
                    AppRoutes.Products,
                    AppRoutes.StockBalances,
                    AppRoutes.LowStock,
                    AppRoutes.Receipt,
                    AppRoutes.Issue,
                    AppRoutes.WriteOff,
                    AppRoutes.Inventory,
                    AppRoutes.OperationHistory,
                    AppRoutes.Reports,
                    AppRoutes.Profile,
                ),
            UserRole.STOREKEEPER to
                setOf(
                    AppRoutes.Categories,
                    AppRoutes.Products,
                    AppRoutes.StockBalances,
                    AppRoutes.LowStock,
                    AppRoutes.Receipt,
                    AppRoutes.Issue,
                    AppRoutes.WriteOff,
                    AppRoutes.Inventory,
                    AppRoutes.OperationHistory,
                    AppRoutes.Profile,
                ),
            UserRole.MANAGER to
                setOf(
                    AppRoutes.Categories,
                    AppRoutes.Products,
                    AppRoutes.StockBalances,
                    AppRoutes.LowStock,
                    AppRoutes.OperationHistory,
                    AppRoutes.Reports,
                    AppRoutes.Profile,
                ),
        )

    @Test
    fun buildDashboardSections_matchesDocumentedMatrix() {
        UserRole.entries.forEach { role ->
            val actual = buildDashboardSections(role).map { it.route }.toSet()
            assertEquals(
                "Dashboard routes for $role",
                expectedRoutesByRole.getValue(role),
                actual,
            )
        }
    }

    @Test
    fun rolePermissions_flags_alignWithDashboardRoutes() {
        UserRole.entries.forEach { role ->
            val routes = buildDashboardSections(role).map { it.route }.toSet()
            assertEquals(RolePermissions.canOpenUsers(role), AppRoutes.Users in routes)
            assertEquals(RolePermissions.canOpenCategories(role), AppRoutes.Categories in routes)
            assertEquals(RolePermissions.canOpenProducts(role), AppRoutes.Products in routes)
            assertEquals(RolePermissions.canOpenStockBalances(role), AppRoutes.StockBalances in routes)
            assertEquals(RolePermissions.canOpenLowStock(role), AppRoutes.LowStock in routes)
            assertEquals(RolePermissions.canCreateStockOperations(role), AppRoutes.Receipt in routes)
            assertEquals(RolePermissions.canCreateStockOperations(role), AppRoutes.Issue in routes)
            assertEquals(RolePermissions.canCreateStockOperations(role), AppRoutes.WriteOff in routes)
            assertEquals(RolePermissions.canCreateStockOperations(role), AppRoutes.Inventory in routes)
            assertEquals(RolePermissions.canOpenOperationHistory(role), AppRoutes.OperationHistory in routes)
            assertEquals(RolePermissions.canOpenReports(role), AppRoutes.Reports in routes)
            assertEquals(RolePermissions.canOpenProfile(role), AppRoutes.Profile in routes)
        }
    }
}
