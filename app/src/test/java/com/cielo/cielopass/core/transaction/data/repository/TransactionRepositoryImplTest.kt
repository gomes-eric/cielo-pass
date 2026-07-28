package com.cielo.cielopass.core.transaction.data.repository

import com.cielo.cielopass.core.database.dao.TransactionDAO
import com.cielo.cielopass.core.database.entity.TransactionEntity
import com.cielo.cielopass.core.transaction.domain.model.Transaction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TransactionRepositoryImplTest {
    private lateinit var dao: TransactionDAO
    private lateinit var repository: TransactionRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk()
        repository = TransactionRepositoryImpl(dao)
    }

    @Test
    fun `given transaction when insert called then map and call dao insert`() =
        runTest {
            // GIVEN
            val transaction = Transaction(id = "tx-1", amount = 1000L, status = "PENDING")
            coEvery { dao.insert(any<TransactionEntity>()) } just runs

            // WHEN
            repository.insert(transaction)

            // THEN
            coVerify(exactly = 1) { dao.insert(any<TransactionEntity>()) }
        }

    @Test
    fun `given transaction when insertIfNoPending called then pass to dao and return boolean result`() =
        runTest {
            // GIVEN
            val transaction = Transaction(id = "tx-1", amount = 1000L, status = "PENDING")
            coEvery { dao.insertIfNoPending(any<TransactionEntity>()) } returns true

            // WHEN
            val result = repository.insertIfNoPending(transaction)

            // THEN
            assertTrue(result)
            coVerify(exactly = 1) { dao.insertIfNoPending(any<TransactionEntity>()) }
        }

    @Test
    fun `given id when getById called then return mapped domain transaction`() =
        runTest {
            // GIVEN
            val entity = TransactionEntity(id = "tx-123", amount = 2000L, status = "APPROVED")
            coEvery { dao.getById("tx-123") } returns entity

            // WHEN
            val result = repository.getById("tx-123")

            // THEN
            assertEquals("tx-123", result?.id)
            assertEquals("APPROVED", result?.status)
        }

    @Test
    fun `given pending transaction in dao when getPending called then return mapped domain model`() =
        runTest {
            // GIVEN
            val entity = TransactionEntity(id = "pending-123", amount = 3000L, status = "PENDING")
            coEvery { dao.getPending() } returns entity

            // WHEN
            val result = repository.getPending()

            // THEN
            assertEquals("pending-123", result?.id)
            assertEquals("PENDING", result?.status)
        }

    @Test
    fun `given no pending transaction in dao when getPending called then return null`() =
        runTest {
            // GIVEN
            coEvery { dao.getPending() } returns null

            // WHEN
            val result = repository.getPending()

            // THEN
            assertNull(result)
        }

    @Test
    fun `given observeAll flow in dao when collected then emit mapped domain list`() =
        runTest {
            // GIVEN
            val entities = listOf(TransactionEntity(id = "tx-1", amount = 1000L, status = "APPROVED"))
            coEvery { dao.observeAll() } returns flowOf(entities)

            // WHEN
            val list = repository.observeAll().first()

            // THEN
            assertEquals(1, list.size)
            assertEquals("tx-1", list[0].id)
        }
}
