package com.cielo.cielopass.core.cielo.domain.model

data class CieloPaymentRequest(
    val clientId: String? = null,
    val accessToken: String? = null,
    val amount: Long,
    val items: List<CieloItem>,
    val reference: String? = null,
    val paymentCode: CieloPaymentCode? = null,
    val installments: Int? = null,
    val email: String? = null,
    val merchantCode: String? = null,
    val subAcquirer: CieloSubAcquirerInfo? = null,
    val eventId: String? = null,
    val quantity: Int = 1,
)
