package com.example.warehouse_accounting_app.domain.usecase.product

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.testutil.FakeProductRepository
import com.example.warehouse_accounting_app.testutil.sampleProduct
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetProductsUseCaseTest {

    @Test
    fun invoke_returns_active_products_from_catalog() {
        runTest {
            val products = listOf(
                sampleProduct(),
                sampleProduct(id = 11L, article = "SKU-002", name = "Гайка М8"),
            )
            val repository = FakeProductRepository(products = products)
            val useCase = GetProductsUseCase(repository)

            val result = useCase(activeOnly = true)

            assertTrue(result is AppResult.Success)
            assertEquals(products, (result as AppResult.Success).data)
            assertEquals(true, repository.lastActiveOnly)
        }
    }
}
