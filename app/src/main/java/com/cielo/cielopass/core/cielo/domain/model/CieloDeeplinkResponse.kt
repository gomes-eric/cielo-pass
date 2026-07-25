package com.cielo.cielopass.core.cielo.domain.model

sealed interface CieloDeeplinkResponse {
    data class Payment(
        val result: CieloPaymentResult,
    ) : CieloDeeplinkResponse

    data class OrdersList(
        val rawJson: String?,
        val error: String?,
    ) : CieloDeeplinkResponse

    data class EnabledProducts(
        val products: List<String>,
        val error: String?,
    ) : CieloDeeplinkResponse

    data class OrderQuery(
        val rawJson: String?,
        val error: String?,
    ) : CieloDeeplinkResponse

    data class TerminalInfo(
        val info: CieloTerminalInfoResult?,
        val error: String?,
    ) : CieloDeeplinkResponse

    data class Establishments(
        val establishments: List<CieloEstablishment>,
        val error: String?,
    ) : CieloDeeplinkResponse

    data class Reversal(
        val result: CieloPaymentResult,
    ) : CieloDeeplinkResponse

    data class Print(
        val isSuccess: Boolean,
        val message: String?,
    ) : CieloDeeplinkResponse

    data class TerminalError(
        val code: Int,
        val message: String?,
    ) : CieloDeeplinkResponse

    data class Unknown(
        val rawUri: String,
    ) : CieloDeeplinkResponse
}
