package com.cielo.cielopass.core.transaction.data.repository

import com.cielo.cielopass.core.database.dao.TransactionDAO
import com.cielo.cielopass.core.transaction.domain.model.Transaction
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val dao: TransactionDAO,
) : TransactionRepository {
    override suspend fun insert(transaction: Transaction) = dao.insert(transaction.toEntity())

    override suspend fun insertIfNoPending(transaction: Transaction): Boolean = dao.insertIfNoPending(transaction.toEntity())

    override suspend fun insert(transactions: List<Transaction>) = dao.insertAll(transactions.map { it.toEntity() })

    override suspend fun update(transaction: Transaction) = dao.update(transaction.toEntity())

    override suspend fun updateStatus(
        id: String,
        status: String,
    ) {
        dao.updateStatus(id, status)
    }

    override suspend fun getById(id: String): Transaction? = dao.getById(id)?.toDomain()

    override suspend fun getAll(): List<Transaction> = dao.getAll().map { it.toDomain() }

    override suspend fun getPending(): Transaction? = dao.getPending()?.toDomain()

    override fun observeAll(): Flow<List<Transaction>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Transaction?> = dao.observeById(id).map { it?.toDomain() }

    override suspend fun deleteById(id: String) = dao.deleteById(id)

    override suspend fun deleteAll() = dao.deleteAll()
}
