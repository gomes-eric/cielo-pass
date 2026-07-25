package com.cielo.cielopass.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cielo.cielopass.core.transaction.domain.model.TransactionItem
import com.cielo.cielopass.core.transaction.domain.model.TransactionPayment

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val orderId: String? = null,
    val status: String,
    val amount: Long,
    val items: List<TransactionItem> = emptyList(),
    val payments: List<TransactionPayment> = emptyList(),
    val rawResponse: String? = null,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
