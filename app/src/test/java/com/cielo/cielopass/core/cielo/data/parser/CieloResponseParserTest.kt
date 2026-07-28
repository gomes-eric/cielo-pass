package com.cielo.cielopass.core.cielo.data.parser

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Base64

class CieloResponseParserTest {
    private lateinit var parser: CieloResponseParser

    @Before
    fun setUp() {
        parser = CieloResponseParser()
    }

    @Test
    fun `given invalid scheme uri when parse then return UnknownResponse`() {
        // GIVEN
        val invalidUri = "http://invalid/response"

        // WHEN
        val result = parser.parse(invalidUri)

        // THEN
        assertTrue(result is CieloDeeplinkResponse.Unknown)
    }

    @Test
    fun `given code-only response query params when parse then return Payment with corresponding result`() {
        // GIVEN
        val uri = "order://response?responsecode=0&id=order-123&reference=ref-456"

        // WHEN
        val result = parser.parse(uri)

        // THEN
        assertTrue(result is CieloDeeplinkResponse.Payment)
        val paymentResult = (result as CieloDeeplinkResponse.Payment).result
        assertTrue(paymentResult is CieloPaymentResult.Approved)
        val approved = paymentResult as CieloPaymentResult.Approved
        assertEquals("order-123", approved.orderId)
        assertEquals("ref-456", approved.reference)
    }

    @Test
    fun `given base64 encoded approved payment json when parse then return Payment Approved`() {
        // GIVEN
        val jsonPayload =
            """
            {
                "id": "order-999",
                "code": 0,
                "reference": "ref-999",
                "paidAmount": 2500,
                "items": [{"id": "item-1", "sku": "sku-1"}],
                "payments": [{"id": "pay-1", "authCode": "123456", "cieloCode": "654321"}]
            }
            """.trimIndent()
        val base64Json = Base64.getEncoder().encodeToString(jsonPayload.toByteArray())
        val uri = "order://response?response=$base64Json&responsecode=0"

        // WHEN
        val result = parser.parse(uri)

        // THEN
        assertTrue(result is CieloDeeplinkResponse.Payment)
        val approved = (result as CieloDeeplinkResponse.Payment).result as CieloPaymentResult.Approved
        assertEquals("order-999", approved.orderId)
        assertEquals(2500L, approved.amount)
        assertEquals(1, approved.items.size)
        assertEquals(1, approved.payments.size)
    }

    @Test
    fun `given base64 encoded cancelled payment json when parse then return Payment Cancelled`() {
        // GIVEN
        val jsonPayload =
            """
            {
                "id": "order-888",
                "code": 1,
                "reason": "User cancelled on POS screen"
            }
            """.trimIndent()
        val base64Json = Base64.getEncoder().encodeToString(jsonPayload.toByteArray())
        val uri = "order://response?response=$base64Json&responsecode=1"

        // WHEN
        val result = parser.parse(uri)

        // THEN
        assertTrue(result is CieloDeeplinkResponse.Payment)
        val cancelled = (result as CieloDeeplinkResponse.Payment).result as CieloPaymentResult.Cancelled
        assertEquals("User cancelled on POS screen", cancelled.reason)
    }

    @Test
    fun `given terminal info json when parse then return TerminalInfo response`() {
        // GIVEN
        val jsonPayload =
            """
            {
                "batteryLevel": 85.0,
                "deviceModel": "LIO V2",
                "logicNumber": "12345678",
                "serialNumber": "SN-9999"
            }
            """.trimIndent()
        val base64Json = Base64.getEncoder().encodeToString(jsonPayload.toByteArray())
        val uri = "order://response?response=$base64Json&responsecode=0"

        // WHEN
        val result = parser.parse(uri)

        // THEN
        assertTrue(result is CieloDeeplinkResponse.TerminalInfo)
        val info = (result as CieloDeeplinkResponse.TerminalInfo).info
        assertEquals("LIO V2", info?.deviceModel)
        assertEquals("12345678", info?.logicNumber)
    }

    @Test
    fun `given establishments json array when parse then return Establishments response`() {
        // GIVEN
        val jsonPayload =
            """
            [
                {"code": "est-1", "name": "Store Alpha"},
                {"code": "est-2", "name": "Store Beta"}
            ]
            """.trimIndent()
        val base64Json = Base64.getEncoder().encodeToString(jsonPayload.toByteArray())
        val uri = "order://response?response=$base64Json&responsecode=0"

        // WHEN
        val result = parser.parse(uri)

        // THEN
        assertTrue(result is CieloDeeplinkResponse.Establishments)
        val list = (result as CieloDeeplinkResponse.Establishments).establishments
        assertEquals(2, list.size)
        assertEquals("Store Alpha", list[0].name)
    }
}
