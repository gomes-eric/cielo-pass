package com.cielo.cielopass.core.transaction.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionItem(
    val id: String,
    val sku: String,
)
