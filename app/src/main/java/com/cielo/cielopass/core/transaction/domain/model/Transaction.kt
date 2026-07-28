package com.cielo.cielopass.core.transaction.domain.model

data class Transaction(
    val id: String,
    val orderId: String? = null,
    val status: String,
    val amount: Long,
    val items: List<TransactionItem> = emptyList(),
    val payments: List<TransactionPayment> = emptyList(),
    val rawResponse: String? = null,
    val errorMessage: String? = null,
    val eventId: String? = null,
    val quantity: Int = 1,
    val inventoryDeducted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
