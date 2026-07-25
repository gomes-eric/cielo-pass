package com.cielo.cielopass.core.cielo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CieloPaymentRequestDTO(
    @SerialName("clientID") val clientId: String,
    @SerialName("accessToken") val accessToken: String,
    @SerialName("value") val value: String,
    @SerialName("items") val items: List<CieloItemDTO>,
    @SerialName("reference") val reference: String? = null,
    @SerialName("merchantCode") val merchantCode: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("installments") val installments: Int? = null,
    @SerialName("paymentCode") val paymentCode: String? = null,
    @SerialName("subAcquirer") val subAcquirer: CieloSubAcquirerDTO? = null,
)
