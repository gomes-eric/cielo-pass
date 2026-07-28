package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.model.CieloItem
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import com.cielo.cielopass.core.credentials.domain.model.CieloCredentials
import com.cielo.cielopass.core.credentials.domain.repository.CieloCredentialsRepository
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LaunchCieloPaymentUseCaseTest {
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var cieloRepository: CieloDeeplinkRepository
    private lateinit var credentialsRepository: CieloCredentialsRepository
    private lateinit var useCase: LaunchCieloPaymentUseCase

    @Before
    fun setUp() {
        transactionRepository = mockk()
        cieloRepository = mockk()
        credentialsRepository = mockk()
        useCase = LaunchCieloPaymentUseCase(
            transactionRepository = transactionRepository,
            cieloRepository = cieloRepository,
            credentialsRepository = credentialsRepository,
        )
    }

    @Test
    fun `given no pending transaction when launch payment succeeds then return Success`() =
        runTest {
            // GIVEN
            val credentials = CieloCredentials(clientId = "client-123", accessToken = "token-456")
            coEvery { credentialsRepository.credentials } returns flowOf(credentials)

            val transactionSlot = slot<Transaction>()
            coEvery { transactionRepository.insertIfNoPending(capture(transactionSlot)) } returns true

            val deeplinkSlot = slot<CieloDeeplink.Payment>()
            every { cieloRepository.launchDeeplink(capture(deeplinkSlot)) } just runs

            val request = CieloPaymentRequest(
                amount = 5000L,
                items = listOf(CieloItem(name = "VIP Ticket", quantity = 1, unitPrice = 5000L)),
                eventId = "event-1",
                quantity = 1,
            )

            // WHEN
            val result = useCase(request)

            // THEN
            assertTrue(result is LaunchPaymentResult.Success)

            val insertedTransaction = transactionSlot.captured
            assertEquals(5000L, insertedTransaction.amount)
            assertEquals("PENDING", insertedTransaction.status)
            assertEquals("event-1", insertedTransaction.eventId)

            val launchedPayment = deeplinkSlot.captured
            assertEquals("client-123", launchedPayment.request.clientId)
            assertEquals("token-456", launchedPayment.request.accessToken)
            assertEquals(insertedTransaction.id, launchedPayment.request.reference)

            coVerify(exactly = 1) { transactionRepository.insertIfNoPending(any()) }
            verify(exactly = 1) { cieloRepository.launchDeeplink(any()) }
        }

    @Test
    fun `given active pending transaction exists when launch payment invoked then return ActiveTransactionExists`() =
        runTest {
            // GIVEN
            val credentials = CieloCredentials(clientId = "client-123", accessToken = "token-456")
            coEvery { credentialsRepository.credentials } returns flowOf(credentials)

            coEvery { transactionRepository.insertIfNoPending(any()) } returns false

            val activePending = Transaction(id = "active-tx-999", amount = 3000L, status = "PENDING")
            coEvery { transactionRepository.getPending() } returns activePending

            val request = CieloPaymentRequest(amount = 2000L, items = emptyList())

            // WHEN
            val result = useCase(request)

            // THEN
            assertTrue(result is LaunchPaymentResult.ActiveTransactionExists)
            assertEquals("active-tx-999", (result as LaunchPaymentResult.ActiveTransactionExists).activeTransactionId)

            verify(exactly = 0) { cieloRepository.launchDeeplink(any()) }
        }

    @Test
    fun `given deeplink launch throws exception when launch payment invoked then update transaction status to FAILED and return Error`() =
        runTest {
            // GIVEN
            val credentials = CieloCredentials(clientId = "client-123", accessToken = "token-456")
            coEvery { credentialsRepository.credentials } returns flowOf(credentials)

            coEvery { transactionRepository.insertIfNoPending(any()) } returns true
            every { cieloRepository.launchDeeplink(any()) } throws RuntimeException("Deeplink failed")
            coEvery { transactionRepository.updateStatus(any(), "FAILED") } just runs

            val request = CieloPaymentRequest(amount = 1000L, items = emptyList())

            // WHEN
            val result = useCase(request)

            // THEN
            assertTrue(result is LaunchPaymentResult.Error)
            assertEquals("Deeplink failed", (result as LaunchPaymentResult.Error).message)

            coVerify(exactly = 1) { transactionRepository.updateStatus(any(), "FAILED") }
        }
}
