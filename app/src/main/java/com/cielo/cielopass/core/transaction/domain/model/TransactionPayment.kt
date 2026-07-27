package com.cielo.cielopass.core.transaction.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionPayment(
    val id: String,
    val authCode: String,
    val nsu: String,
)
