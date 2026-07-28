package com.cielo.cielopass.core.cielo.domain.model

data class CieloReversalRequest(
    val clientId: String,
    val accessToken: String,
    val orderId: String,
    val value: Long,
    val cieloCode: String,
    val authCode: String,
)
