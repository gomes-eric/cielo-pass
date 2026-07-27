package com.cielo.cielopass.core.transaction.domain.repository

import com.cielo.cielopass.core.transaction.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun insert(transaction: Transaction)

    suspend fun insertIfNoPending(transaction: Transaction): Boolean

    suspend fun insert(transactions: List<Transaction>)

    suspend fun update(transaction: Transaction)

    suspend fun updateStatus(
        id: String,
        status: String,
    )

    suspend fun getById(id: String): Transaction?

    suspend fun getAll(): List<Transaction>

    suspend fun getPending(): Transaction?

    fun observeAll(): Flow<List<Transaction>>

    fun observeById(id: String): Flow<Transaction?>

    suspend fun deleteById(id: String)

    suspend fun deleteAll()
}
