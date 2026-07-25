package com.cielo.cielopass.core.cielo.data.dto

import com.cielo.cielopass.core.constants.ItemConstants.DEFAULT_ITEM_UNIT_OF_MEASURE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CieloItemDTO(
    @SerialName("name") val name: String,
    @SerialName("quantity") val quantity: Int,
    @SerialName("sku") val sku: String,
    @SerialName("unitOfMeasure") val unitOfMeasure: String = DEFAULT_ITEM_UNIT_OF_MEASURE,
    @SerialName("unitPrice") val unitPrice: Long,
)
