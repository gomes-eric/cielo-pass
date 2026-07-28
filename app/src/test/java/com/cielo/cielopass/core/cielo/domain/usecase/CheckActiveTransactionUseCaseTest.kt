package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.transaction.domain.model.Transaction
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CheckActiveTransactionUseCaseTest {
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var useCase: CheckActiveTransactionUseCase

    @Before
    fun setUp() {
        transactionRepository = mockk()
        useCase = CheckActiveTransactionUseCase(transactionRepository)
    }

    @Test
    fun `given pending transaction exists when invoke then return active transaction`() =
        runTest {
            // GIVEN
            val pendingTransaction = Transaction(
                id = "tx-123",
                amount = 1000L,
                status = "PENDING",
                eventId = "event-456",
                quantity = 2,
            )
            coEvery { transactionRepository.getPending() } returns pendingTransaction

            // WHEN
            val result = useCase()

            // THEN
            assertEquals(pendingTransaction, result)
            coVerify(exactly = 1) { transactionRepository.getPending() }
        }

    @Test
    fun `given no pending transaction when invoke then return null`() =
        runTest {
            // GIVEN
            coEvery { transactionRepository.getPending() } returns null

            // WHEN
            val result = useCase()

            // THEN
            assertNull(result)
            coVerify(exactly = 1) { transactionRepository.getPending() }
        }
}
