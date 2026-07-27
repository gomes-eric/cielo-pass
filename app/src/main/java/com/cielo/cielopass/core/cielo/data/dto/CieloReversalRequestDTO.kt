package com.cielo.cielopass.core.cielo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CieloReversalRequestDTO(
    @SerialName("id") val id: String,
    @SerialName("clientID") val clientId: String? = null,
    @SerialName("accessToken") val accessToken: String? = null,
    @SerialName("cieloCode") val cieloCode: String,
    @SerialName("authCode") val authCode: String,
    @SerialName("value") val value: Long,
)
