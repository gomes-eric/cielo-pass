package com.cielo.cielopass.core.cielo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CieloEstablishmentDTO(
    @SerialName("code") val code: String? = null,
    @SerialName("name") val name: String? = null,
)
