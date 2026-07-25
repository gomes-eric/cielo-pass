package com.cielo.cielopass.core.cielo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CieloPrintRequestDTO(
    @SerialName("operation") val operation: String,
    @SerialName("styles") val styles: List<Map<String, Int>> = listOf(emptyMap()),
    @SerialName("value") val value: List<String>,
)
