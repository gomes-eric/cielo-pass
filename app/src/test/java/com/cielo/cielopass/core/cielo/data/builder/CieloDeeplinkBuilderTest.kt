package com.cielo.cielopass.core.cielo.data.builder

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.model.CieloItem
import com.cielo.cielopass.core.cielo.domain.model.CieloOrderQueryRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloOrdersListRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPrintRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloReversalRequest
import com.cielo.cielopass.core.constants.CieloConstants.BASE_URI_ORDER
import com.cielo.cielopass.core.constants.CieloConstants.BASE_URI_ORDERS
import com.cielo.cielopass.core.constants.CieloConstants.BASE_URI_PAYMENT
import com.cielo.cielopass.core.constants.CieloConstants.BASE_URI_PRINT
import com.cielo.cielopass.core.constants.CieloConstants.BASE_URI_REVERSAL
import com.cielo.cielopass.core.constants.CieloConstants.URI_ENABLED_PRODUCTS
import com.cielo.cielopass.core.constants.CieloConstants.URI_ESTABLISHMENTS
import com.cielo.cielopass.core.constants.CieloConstants.URI_TERMINAL_INFO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CieloDeeplinkBuilderTest {
    private lateinit var builder: CieloDeeplinkBuilder

    @Before
    fun setUp() {
        builder = CieloDeeplinkBuilder()
    }

    @Test
    fun `given payment request when buildPaymentUriString then return correct base uri prefix and callback suffix`() {
        // GIVEN
        val request = CieloPaymentRequest(
            amount = 1000L,
            items = listOf(CieloItem(name = "Ticket", quantity = 1, unitPrice = 1000L)),
            reference = "ref-123",
        )

        // WHEN
        val uriString = builder.buildPaymentUriString(request)

        // THEN
        assertTrue(uriString.startsWith(BASE_URI_PAYMENT))
        assertTrue(uriString.endsWith("&urlCallback=order://response"))
    }

    @Test
    fun `given orders list request when buildOrdersListUriString then return correct uri`() {
        // GIVEN
        val request = CieloOrdersListRequest(clientId = "client-123", accessToken = "token-456", pageSize = 5, page = 1)

        // WHEN
        val uriString = builder.buildOrdersListUriString(request)

        // THEN
        assertTrue(uriString.startsWith(BASE_URI_ORDERS))
        assertTrue(uriString.endsWith("&urlCallback=order://response"))
    }

    @Test
    fun `given order query request when buildOrderQueryUriString then return correct uri`() {
        // GIVEN
        val request = CieloOrderQueryRequest(orderId = "order-123", amount = 5000L)

        // WHEN
        val uriString = builder.buildOrderQueryUriString(request)

        // THEN
        assertTrue(uriString.startsWith(BASE_URI_ORDER))
        assertTrue(uriString.endsWith("&urlCallback=order://response"))
    }

    @Test
    fun `given reversal request when buildReversalUriString then return correct uri`() {
        // GIVEN
        val request = CieloReversalRequest(
            clientId = "client-123",
            accessToken = "token-456",
            orderId = "order-123",
            value = 5000L,
            cieloCode = "cielo-code",
            authCode = "auth-code",
        )

        // WHEN
        val uriString = builder.buildReversalUriString(request)

        // THEN
        assertTrue(uriString.startsWith(BASE_URI_REVERSAL))
        assertTrue(uriString.endsWith("&urlCallback=order://response"))
    }

    @Test
    fun `given print text request when buildPrintUriString then return correct uri`() {
        // GIVEN
        val request = CieloPrintRequest.Text(text = "Hello Print")

        // WHEN
        val uriString = builder.buildPrintUriString(request)

        // THEN
        assertTrue(uriString.startsWith(BASE_URI_PRINT))
        assertTrue(uriString.endsWith("&urlCallback=order://response"))
    }

    @Test
    fun `given static endpoints when build URI strings then return constant URIs`() {
        // GIVEN / WHEN / THEN
        assertEquals(URI_ENABLED_PRODUCTS, builder.buildUriString(CieloDeeplink.EnabledProducts))
        assertEquals(URI_TERMINAL_INFO, builder.buildUriString(CieloDeeplink.TerminalInfo))
        assertEquals(URI_ESTABLISHMENTS, builder.buildUriString(CieloDeeplink.Establishments))
    }
}
