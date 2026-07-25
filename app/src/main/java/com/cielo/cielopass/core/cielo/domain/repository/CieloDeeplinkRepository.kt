package com.cielo.cielopass.core.cielo.domain.repository

import android.net.Uri
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloOrderQueryRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloOrdersListRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPrintRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloReversalRequest

interface CieloDeeplinkRepository {
    fun buildUri(deeplink: CieloDeeplink): Uri

    fun buildPaymentUri(request: CieloPaymentRequest): Uri

    fun buildOrdersListUri(request: CieloOrdersListRequest): Uri

    fun buildEnabledProductsUri(): Uri

    fun buildOrderQueryUri(request: CieloOrderQueryRequest): Uri

    fun buildTerminalInfoUri(): Uri

    fun buildEstablishmentsUri(): Uri

    fun buildReversalUri(request: CieloReversalRequest): Uri

    fun buildPrintUri(request: CieloPrintRequest): Uri

    fun parseResponseUri(uriString: String): CieloDeeplinkResponse

    fun launchDeeplink(deeplink: CieloDeeplink)

    fun stopPaymentService()
}
