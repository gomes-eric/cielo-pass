package com.cielo.cielopass.core.transaction.data.repository

import com.cielo.cielopass.core.database.entity.TransactionEntity
import com.cielo.cielopass.core.transaction.domain.model.Transaction
import com.cielo.cielopass.core.transaction.domain.model.TransactionItem
import com.cielo.cielopass.core.transaction.domain.model.TransactionPayment
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionMapperTest {
    @Test
    fun `given TransactionEntity when mapped toDomain then return matching Transaction`() {
        // GIVEN
        val items = listOf(TransactionItem(id = "i1", sku = "sku1"))
        val payments = listOf(TransactionPayment(id = "p1", authCode = "123", nsu = "456"))
        val entity = TransactionEntity(
            id = "tx1",
            orderId = "ord1",
            status = "APPROVED",
            amount = 5000L,
            items = items,
            payments = payments,
            rawResponse = "raw",
            errorMessage = null,
            eventId = "e1",
            quantity = 2,
            inventoryDeducted = true,
            createdAt = 100L,
            updatedAt = 200L,
        )

        // WHEN
        val domain = entity.toDomain()

        // THEN
        assertEquals("tx1", domain.id)
        assertEquals("ord1", domain.orderId)
        assertEquals("APPROVED", domain.status)
        assertEquals(5000L, domain.amount)
        assertEquals(items, domain.items)
        assertEquals(payments, domain.payments)
        assertEquals("raw", domain.rawResponse)
        assertEquals("e1", domain.eventId)
        assertEquals(2, domain.quantity)
        assertEquals(true, domain.inventoryDeducted)
        assertEquals(100L, domain.createdAt)
        assertEquals(200L, domain.updatedAt)
    }

    @Test
    fun `given Transaction domain model when mapped toEntity then return matching TransactionEntity`() {
        // GIVEN
        val items = listOf(TransactionItem(id = "i1", sku = "sku1"))
        val payments = listOf(TransactionPayment(id = "p1", authCode = "123", nsu = "456"))
        val domain = Transaction(
            id = "tx1",
            orderId = "ord1",
            status = "APPROVED",
            amount = 5000L,
            items = items,
            payments = payments,
            rawResponse = "raw",
            errorMessage = null,
            eventId = "e1",
            quantity = 2,
            inventoryDeducted = true,
            createdAt = 100L,
            updatedAt = 200L,
        )

        // WHEN
        val entity = domain.toEntity()

        // THEN
        assertEquals("tx1", entity.id)
        assertEquals("ord1", entity.orderId)
        assertEquals("APPROVED", entity.status)
        assertEquals(5000L, entity.amount)
        assertEquals(items, entity.items)
        assertEquals(payments, entity.payments)
        assertEquals("raw", entity.rawResponse)
        assertEquals("e1", entity.eventId)
        assertEquals(2, entity.quantity)
        assertEquals(true, entity.inventoryDeducted)
        assertEquals(100L, entity.createdAt)
        assertEquals(200L, entity.updatedAt)
    }
}
