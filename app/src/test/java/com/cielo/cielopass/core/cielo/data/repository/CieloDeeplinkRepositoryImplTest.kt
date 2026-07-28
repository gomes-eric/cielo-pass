package com.cielo.cielopass.core.cielo.data.repository

import android.content.Context
import android.net.Uri
import com.cielo.cielopass.core.cielo.data.builder.CieloDeeplinkBuilder
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloOrderQueryRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloOrdersListRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPrintRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloReversalRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CieloDeeplinkRepositoryImplTest {
    private lateinit var context: Context
    private lateinit var builder: CieloDeeplinkBuilder
    private lateinit var parser: CieloResponseParser
    private lateinit var repository: CieloDeeplinkRepositoryImpl

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        builder = mockk()
        parser = mockk()
        repository = CieloDeeplinkRepositoryImpl(
            context = context,
            builder = builder,
            parser = parser,
        )
    }

    @Test
    fun `given deeplink when buildUri then delegate to builder`() {
        // GIVEN
        val mockUri = mockk<Uri>()
        val deeplink = CieloDeeplink.EnabledProducts
        every { builder.buildUri(deeplink) } returns mockUri

        // WHEN
        val result = repository.buildUri(deeplink)

        // THEN
        assertEquals(mockUri, result)
        verify(exactly = 1) { builder.buildUri(deeplink) }
    }

    @Test
    fun `given payment request when buildPaymentUri then delegate to builder`() {
        // GIVEN
        val mockUri = mockk<Uri>()
        val request = CieloPaymentRequest(amount = 1000L, items = emptyList())
        every { builder.buildPaymentUri(request) } returns mockUri

        // WHEN
        val result = repository.buildPaymentUri(request)

        // THEN
        assertEquals(mockUri, result)
        verify(exactly = 1) { builder.buildPaymentUri(request) }
    }

    @Test
    fun `given orders list request when buildOrdersListUri then delegate to builder`() {
        // GIVEN
        val mockUri = mockk<Uri>()
        val request = CieloOrdersListRequest(clientId = "client", accessToken = "token", pageSize = 5, page = 1)
        every { builder.buildOrdersListUri(request) } returns mockUri

        // WHEN
        val result = repository.buildOrdersListUri(request)

        // THEN
        assertEquals(mockUri, result)
        verify(exactly = 1) { builder.buildOrdersListUri(request) }
    }

    @Test
    fun `given order query request when buildOrderQueryUri then delegate to builder`() {
        // GIVEN
        val mockUri = mockk<Uri>()
        val request = CieloOrderQueryRequest(orderId = "ord-1")
        every { builder.buildOrderQueryUri(request) } returns mockUri

        // WHEN
        val result = repository.buildOrderQueryUri(request)

        // THEN
        assertEquals(mockUri, result)
        verify(exactly = 1) { builder.buildOrderQueryUri(request) }
    }

    @Test
    fun `given reversal request when buildReversalUri then delegate to builder`() {
        // GIVEN
        val mockUri = mockk<Uri>()
        val request = CieloReversalRequest(clientId = "client", accessToken = "token", orderId = "ord-1", value = 1000L, cieloCode = "cc", authCode = "ac")
        every { builder.buildReversalUri(request) } returns mockUri

        // WHEN
        val result = repository.buildReversalUri(request)

        // THEN
        assertEquals(mockUri, result)
        verify(exactly = 1) { builder.buildReversalUri(request) }
    }

    @Test
    fun `given print request when buildPrintUri then delegate to builder`() {
        // GIVEN
        val mockUri = mockk<Uri>()
        val request = CieloPrintRequest.Text("Receipt")
        every { builder.buildPrintUri(request) } returns mockUri

        // WHEN
        val result = repository.buildPrintUri(request)

        // THEN
        assertEquals(mockUri, result)
        verify(exactly = 1) { builder.buildPrintUri(request) }
    }

    @Test
    fun `given uri string when parseResponseUri then delegate to parser`() {
        // GIVEN
        val uriStr = "order://response?response=xyz"
        val mockResponse = mockk<CieloDeeplinkResponse>()
        every { parser.parse(uriStr) } returns mockResponse

        // WHEN
        val result = repository.parseResponseUri(uriStr)

        // THEN
        assertEquals(mockResponse, result)
        verify(exactly = 1) { parser.parse(uriStr) }
    }
}
