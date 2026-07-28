package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResultItem
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResultPayment
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import com.cielo.cielopass.core.transaction.domain.model.Transaction
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProcessCieloResponseUseCaseTest {
    private lateinit var cieloRepository: CieloDeeplinkRepository
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var useCase: ProcessCieloResponseUseCase

    @Before
    fun setUp() {
        cieloRepository = mockk()
        transactionRepository = mockk()
        useCase = ProcessCieloResponseUseCase(
            cieloRepository = cieloRepository,
            transactionRepository = transactionRepository,
        )
        every { cieloRepository.stopPaymentService() } just runs
    }

    @Test
    fun `given approved payment response when processed then stop service and update pending transaction to APPROVED`() =
        runTest {
            // GIVEN
            val uri = "order://response?response=xyz"
            val reference = "ref-123"
            val approvedResult = CieloPaymentResult.Approved(
                orderId = "order-789",
                reference = reference,
                amount = 5000L,
                items = listOf(CieloPaymentResultItem(id = "item-1", sku = "sku-1")),
                payments = listOf(CieloPaymentResultPayment(id = "pay-1", authCode = "123456", nsu = "987654")),
                rawResponse = "raw-json",
            )
            val response = CieloDeeplinkResponse.Payment(approvedResult)

            every { cieloRepository.parseResponseUri(uri) } returns response

            val pendingTx = Transaction(
                id = reference,
                amount = 5000L,
                status = "PENDING",
            )
            coEvery { transactionRepository.getById(reference) } returns pendingTx

            val txSlot = slot<Transaction>()
            coEvery { transactionRepository.update(capture(txSlot)) } just runs

            // WHEN
            val result = useCase(uri)

            // THEN
            assertEquals(response, result)
            verify(exactly = 1) { cieloRepository.stopPaymentService() }

            val updatedTx = txSlot.captured
            assertEquals("APPROVED", updatedTx.status)
            assertEquals("order-789", updatedTx.orderId)
            assertEquals(5000L, updatedTx.amount)
            assertEquals(1, updatedTx.items.size)
            assertEquals(1, updatedTx.payments.size)

            coVerify(exactly = 1) { transactionRepository.update(any()) }
        }

    @Test
    fun `given cancelled payment response when processed then update pending transaction to CANCELLED`() =
        runTest {
            // GIVEN
            val uri = "order://response?response=xyz"
            val cancelledResult = CieloPaymentResult.Cancelled(code = 1, reason = "User cancelled")
            val response = CieloDeeplinkResponse.Payment(cancelledResult)

            every { cieloRepository.parseResponseUri(uri) } returns response

            val pendingTx = Transaction(id = "tx-pending-1", amount = 2000L, status = "PENDING")
            coEvery { transactionRepository.getPending() } returns pendingTx

            val txSlot = slot<Transaction>()
            coEvery { transactionRepository.update(capture(txSlot)) } just runs

            // WHEN
            val result = useCase(uri)

            // THEN
            assertEquals(response, result)
            val updated = txSlot.captured
            assertEquals("CANCELLED", updated.status)
            assertEquals("User cancelled", updated.errorMessage)
        }

    @Test
    fun `given failed payment response when processed then update pending transaction to FAILED`() =
        runTest {
            // GIVEN
            val uri = "order://response?response=xyz"
            val failedResult = CieloPaymentResult.Failed(code = 2, reason = "Card declined")
            val response = CieloDeeplinkResponse.Payment(failedResult)

            every { cieloRepository.parseResponseUri(uri) } returns response

            val pendingTx = Transaction(id = "tx-pending-2", amount = 1000L, status = "PENDING")
            coEvery { transactionRepository.getPending() } returns pendingTx

            val txSlot = slot<Transaction>()
            coEvery { transactionRepository.update(capture(txSlot)) } just runs

            // WHEN
            val result = useCase(uri)

            // THEN
            assertEquals(response, result)
            val updated = txSlot.captured
            assertEquals("FAILED", updated.status)
            assertEquals("Card declined", updated.errorMessage)
        }

    @Test
    fun `given terminal error response when processed then update pending transaction to FAILED with error message`() =
        runTest {
            // GIVEN
            val uri = "order://response?response=xyz"
            val response = CieloDeeplinkResponse.TerminalError(code = 99, message = "Terminal hardware failure")

            every { cieloRepository.parseResponseUri(uri) } returns response

            val pendingTx = Transaction(id = "tx-pending-3", amount = 0L, status = "PENDING")
            coEvery { transactionRepository.getPending() } returns pendingTx

            val txSlot = slot<Transaction>()
            coEvery { transactionRepository.update(capture(txSlot)) } just runs

            // WHEN
            val result = useCase(uri)

            // THEN
            assertTrue(result is CieloDeeplinkResponse.TerminalError)
            val updated = txSlot.captured
            assertEquals("FAILED", updated.status)
            assertEquals("Terminal hardware failure", updated.errorMessage)
        }
}
