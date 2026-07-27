package com.cielo.cielopass.core.cielo.domain.model

sealed interface CieloPaymentResult {
    data class Approved(
        val orderId: String,
        val amount: Long,
        val items: List<CieloPaymentResultItem> = emptyList(),
        val payments: List<CieloPaymentResultPayment> = emptyList(),
        val rawResponse: String? = null,
    ) : CieloPaymentResult

    data class Cancelled(
        val orderId: String,
        val code: Int,
        val reason: String?,
    ) : CieloPaymentResult

    data class Failed(
        val orderId: String,
        val code: Int,
        val reason: String?,
        val message: String?,
    ) : CieloPaymentResult

    data class Unknown(
        val rawUri: String,
        val error: String,
    ) : CieloPaymentResult
}
