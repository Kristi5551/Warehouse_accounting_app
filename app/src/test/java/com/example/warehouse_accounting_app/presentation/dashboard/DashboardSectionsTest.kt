package com.example.warehouse_accounting_app.presentation.dashboard

import com.example.warehouse_accounting_app.core.navigation.AppRoutes
import com.example.warehouse_accounting_app.domain.model.UserRole
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardSectionsTest {

    @Test
    fun buildDashboardSections_reflects_storekeeper_warehouse_access() {
        val routes = buildDashboardSections(UserRole.STOREKEEPER).map { it.route }

        assertFalse(AppRoutes.Users in routes)
        assertTrue(AppRoutes.Receipt in routes)
        assertTrue(AppRoutes.Issue in routes)
        assertFalse(AppRoutes.Reports in routes)
    }
}
