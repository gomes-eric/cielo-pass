package com.cielo.cielopass.core.cielo.domain.model

data class CieloTerminalInfoResult(
    val batteryLevel: Double = 0.0,
    val deviceModel: String? = null,
    val imeiNumber: String? = null,
    val logicNumber: String? = null,
    val merchantCode: String? = null,
    val serialNumber: String? = null,
)
