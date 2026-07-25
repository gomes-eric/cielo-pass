package com.cielo.cielopass.core.cielo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CieloOrderQueryRequestDTO(
    @SerialName("orderId") val orderId: String? = null,
    @SerialName("amount") val amount: Long? = null,
    @SerialName("authCode") val authCode: String? = null,
    @SerialName("cieloCode") val cieloCode: String? = null,
    @SerialName("clientID") val clientId: String? = null,
    @SerialName("accessToken") val accessToken: String? = null,
)
