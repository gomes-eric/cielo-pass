package com.cielo.cielopass.core.cielo.domain.model

data class CieloReversalRequest(
    val orderId: String,
    val value: Long,
    val cieloCode: String,
    val authCode: String,
    val clientId: String? = null,
    val accessToken: String? = null,
)
