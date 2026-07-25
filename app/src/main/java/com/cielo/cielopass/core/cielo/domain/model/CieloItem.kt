package com.cielo.cielopass.core.cielo.domain.model

import com.cielo.cielopass.core.constants.ItemConstants.DEFAULT_ITEM_NAME
import com.cielo.cielopass.core.constants.ItemConstants.DEFAULT_ITEM_SKU
import com.cielo.cielopass.core.constants.ItemConstants.DEFAULT_ITEM_UNIT_OF_MEASURE

data class CieloItem(
    val name: String = DEFAULT_ITEM_NAME,
    val quantity: Int = 1,
    val sku: String = DEFAULT_ITEM_SKU,
    val unitOfMeasure: String = DEFAULT_ITEM_UNIT_OF_MEASURE,
    val unitPrice: Long,
)
