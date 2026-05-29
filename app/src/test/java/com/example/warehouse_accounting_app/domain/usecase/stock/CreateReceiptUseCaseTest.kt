package com.example.warehouse_accounting_app.domain.usecase.stock

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.testutil.FakeStockRepository
import com.example.warehouse_accounting_app.testutil.sampleProduct
import com.example.warehouse_accounting_app.testutil.sampleReceiptOperation
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateReceiptUseCaseTest {

    @Test
    fun invoke_creates_receipt_on_warehouse() {
        runTest {
            val operation = sampleReceiptOperation(id = 42L)
            val repository = FakeStockRepository(receiptResult = AppResult.Success(operation))
            val useCase = CreateReceiptUseCase(repository)

            val result = useCase(
                warehouseId = 1L,
                productId = sampleProduct().id,
                quantity = 25.0,
                price = 120.5,
                supplier = "ООО Поставщик",
                comment = "Плановая поставка",
            )

            assertTrue(result is AppResult.Success)
            assertEquals(operation, (result as AppResult.Success).data)
            assertEquals(
                FakeStockRepository.ReceiptCall(1L, 10L, 25.0, 120.5, "ООО Поставщик", "Плановая поставка"),
                repository.lastReceipt,
            )
        }
    }
}
