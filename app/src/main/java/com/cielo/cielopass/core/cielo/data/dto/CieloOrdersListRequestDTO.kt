package com.cielo.cielopass.core.cielo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CieloOrdersListRequestDTO(
    @SerialName("pageSize") val pageSize: Int = 5,
    @SerialName("page") val page: Int = 0,
    @SerialName("clientID") val clientId: String? = null,
    @SerialName("accessToken") val accessToken: String? = null,
)
