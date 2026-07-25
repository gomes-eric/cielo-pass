package com.cielo.cielopass.core.cielo.domain.model

data class CieloPaymentResultPayment(
    val id: String,
    val authCode: String,
    val nsu: String,
)
