package com.cielo.cielopass.core.cielo.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.cielo.cielopass.core.cielo.data.builder.CieloDeeplinkBuilder
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.Payment
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloOrderQueryRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloOrdersListRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPrintRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloReversalRequest
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import com.cielo.cielopass.core.cielo.service.CieloForegroundService

class CieloDeeplinkRepositoryImpl(
    private val context: Context,
    private val builder: CieloDeeplinkBuilder,
    private val parser: CieloResponseParser,
) : CieloDeeplinkRepository {
    override fun buildUri(deeplink: CieloDeeplink): Uri = builder.buildUri(deeplink)

    override fun buildPaymentUri(request: CieloPaymentRequest): Uri = builder.buildPaymentUri(request)

    override fun buildOrdersListUri(request: CieloOrdersListRequest): Uri = builder.buildOrdersListUri(request)

    override fun buildEnabledProductsUri(): Uri = builder.buildEnabledProductsUri()

    override fun buildOrderQueryUri(request: CieloOrderQueryRequest): Uri = builder.buildOrderQueryUri(request)

    override fun buildTerminalInfoUri(): Uri = builder.buildTerminalInfoUri()

    override fun buildEstablishmentsUri(): Uri = builder.buildEstablishmentsUri()

    override fun buildReversalUri(request: CieloReversalRequest): Uri = builder.buildReversalUri(request)

    override fun buildPrintUri(request: CieloPrintRequest): Uri = builder.buildPrintUri(request)

    override fun parseResponseUri(uriString: String): CieloDeeplinkResponse = parser.parse(uriString)

    override fun launchDeeplink(deeplink: CieloDeeplink) {
        if (deeplink is Payment) {
            CieloForegroundService.start(context)
        }

        val uri = builder.buildUri(deeplink)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }

    override fun stopPaymentService() {
        CieloForegroundService.stop(context)
    }
}
