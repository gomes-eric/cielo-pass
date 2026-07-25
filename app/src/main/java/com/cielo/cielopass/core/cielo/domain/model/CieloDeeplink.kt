package com.cielo.cielopass.core.cielo.domain.model

sealed interface CieloDeeplink {
    data class Payment(
        val request: CieloPaymentRequest,
    ) : CieloDeeplink

    data class OrdersList(
        val request: CieloOrdersListRequest,
    ) : CieloDeeplink

    data object EnabledProducts : CieloDeeplink

    data class OrderQuery(
        val request: CieloOrderQueryRequest,
    ) : CieloDeeplink

    data object TerminalInfo : CieloDeeplink

    data object Establishments : CieloDeeplink

    data class Reversal(
        val request: CieloReversalRequest,
    ) : CieloDeeplink

    data class Print(
        val request: CieloPrintRequest,
    ) : CieloDeeplink
}
