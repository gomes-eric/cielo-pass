package com.cielo.cielopass.core.cielo.domain.model

data class CieloOrdersListRequest(
    val clientId: String,
    val accessToken: String,
    val pageSize: Int = 5,
    val page: Int = 0,
)
