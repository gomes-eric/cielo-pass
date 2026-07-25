package com.cielo.cielopass.core.cielo.domain.model

data class CieloOrdersListRequest(
    val pageSize: Int = 5,
    val page: Int = 0,
    val clientId: String? = null,
    val accessToken: String? = null,
)
