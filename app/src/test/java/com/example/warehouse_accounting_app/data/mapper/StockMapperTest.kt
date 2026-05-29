package com.example.warehouse_accounting_app.data.mapper

import com.example.warehouse_accounting_app.data.remote.dto.response.StockBalanceResponseDto
import com.example.warehouse_accounting_app.domain.model.StockStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class StockMapperTest {

    @Test
    fun stockBalanceDto_maps_api_payload_to_domain_balance() {
        val dto = StockBalanceResponseDto(
            id = 1L,
            productId = 10L,
            productArticle = "SKU-001",
            productName = "Болт М8",
            categoryName = "Крепёж",
            warehouseId = 1L,
            warehouseName = "Основной склад",
            quantity = "3",
            minStock = "10",
            status = "LOW_STOCK",
            updatedAt = "2026-05-29T12:00:00",
        )

        val balance = dto.toDomain()

        assertEquals("SKU-001", balance.productArticle)
        assertEquals(3.0, balance.quantity, 0.001)
        assertEquals(StockStatus.LOW_STOCK, balance.status)
    }
}
