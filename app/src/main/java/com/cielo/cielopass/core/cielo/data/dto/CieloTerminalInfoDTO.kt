package com.cielo.cielopass.core.cielo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CieloTerminalInfoDTO(
    @SerialName("batteryLevel") val batteryLevel: Double? = null,
    @SerialName("deviceModel") val deviceModel: String? = null,
    @SerialName("imeiNumber") val imeiNumber: String? = null,
    @SerialName("logicNumber") val logicNumber: String? = null,
    @SerialName("merchantCode") val merchantCode: String? = null,
    @SerialName("serialNumber") val serialNumber: String? = null,
    @SerialName("code") val code: Int? = null,
    @SerialName("reason") val reason: String? = null,
)
