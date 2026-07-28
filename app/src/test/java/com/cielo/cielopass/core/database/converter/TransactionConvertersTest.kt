package com.cielo.cielopass.core.database.converter

import com.cielo.cielopass.core.transaction.domain.model.TransactionItem
import com.cielo.cielopass.core.transaction.domain.model.TransactionPayment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TransactionConvertersTest {
    private lateinit var converters: TransactionConverters

    @Before
    fun setUp() {
        converters = TransactionConverters()
    }

    @Test
    fun `given items list when converted to string and back then return equal list`() {
        // GIVEN
        val originalItems = listOf(
            TransactionItem(id = "item-1", sku = "sku-1"),
            TransactionItem(id = "item-2", sku = "sku-2"),
        )

        // WHEN
        val jsonString = converters.fromItemList(originalItems)
        val deserializedItems = converters.toItemList(jsonString)

        // THEN
        assertEquals(originalItems, deserializedItems)
    }

    @Test
    fun `given null or blank json when converting to item list then return empty list`() {
        // WHEN / THEN
        assertTrue(converters.toItemList(null).isEmpty())
        assertTrue(converters.toItemList("").isEmpty())
        assertTrue(converters.toItemList("   ").isEmpty())
        assertTrue(converters.toItemList("invalid-json").isEmpty())
    }

    @Test
    fun `given payments list when converted to string and back then return equal list`() {
        // GIVEN
        val originalPayments = listOf(
            TransactionPayment(id = "pay-1", authCode = "123456", nsu = "987654"),
        )

        // WHEN
        val jsonString = converters.fromPaymentList(originalPayments)
        val deserializedPayments = converters.toPaymentList(jsonString)

        // THEN
        assertEquals(originalPayments, deserializedPayments)
    }

    @Test
    fun `given null or blank json when converting to payment list then return empty list`() {
        // WHEN / THEN
        assertTrue(converters.toPaymentList(null).isEmpty())
        assertTrue(converters.toPaymentList("").isEmpty())
        assertTrue(converters.toPaymentList("invalid-json").isEmpty())
    }
}
