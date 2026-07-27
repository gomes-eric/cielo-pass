package com.cielo.cielopass.core.cielo.domain.model

data class CieloOrderQueryRequest(
    val orderId: String,
    val amount: Long? = null,
    val authCode: String? = null,
    val cieloCode: String? = null,
    val clientId: String? = null,
    val accessToken: String? = null,
)
