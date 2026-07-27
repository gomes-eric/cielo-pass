package com.cielo.cielopass.core.cielo.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CieloSubAcquirerDTO(
    @SerialName("softDescriptor") val softDescriptor: String? = null,
    @SerialName("terminalId") val terminalId: String? = null,
    @SerialName("merchantCode") val merchantCode: String? = null,
    @SerialName("city") val city: String? = null,
    @SerialName("telephone") val telephone: String? = null,
    @SerialName("state") val state: String? = null,
    @SerialName("postalCode") val postalCode: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("identifier") val identifier: String? = null,
    @SerialName("merchantCategoryCode") val merchantCategoryCode: String? = null,
    @SerialName("countryCode") val countryCode: String? = null,
    @SerialName("informationType") val informationType: String? = null,
    @SerialName("document") val document: String? = null,
    @SerialName("businessName") val businessName: String? = null,
    @SerialName("ibgeCode") val ibgeCode: String? = null,
)
