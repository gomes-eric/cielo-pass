package com.cielo.cielopass.core.cielo.domain.model

import com.cielo.cielopass.core.constants.CieloConstants.DEFAULT_COUNTRY_CODE
import com.cielo.cielopass.core.constants.CieloConstants.DEFAULT_IBGE_CODE
import com.cielo.cielopass.core.constants.CieloConstants.DEFAULT_INFORMATION_TYPE

data class CieloSubAcquirerInfo(
    val softDescriptor: String? = null,
    val terminalId: String? = null,
    val merchantCode: String? = null,
    val city: String? = null,
    val telephone: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val address: String? = null,
    val identifier: String? = null,
    val merchantCategoryCode: String? = null,
    val countryCode: String? = DEFAULT_COUNTRY_CODE,
    val informationType: String? = DEFAULT_INFORMATION_TYPE,
    val document: String? = null,
    val businessName: String? = null,
    val ibgeCode: String? = DEFAULT_IBGE_CODE,
)
