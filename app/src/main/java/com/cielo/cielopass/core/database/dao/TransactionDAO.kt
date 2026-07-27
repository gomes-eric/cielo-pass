package com.cielo.cielopass.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_PENDING
import com.cielo.cielopass.core.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Query("UPDATE transactions SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(
        id: String,
        status: String,
        updatedAt: Long = System.currentTimeMillis(),
    )

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE status = '${STATUS_PENDING}' LIMIT 1")
    suspend fun getPending(): TransactionEntity?

    @Transaction
    suspend fun insertIfNoPending(transaction: TransactionEntity): Boolean {
        val pending = getPending()
        if (pending != null) return false

        val existing = getById(transaction.id)
        if (existing != null) {
            val updated = existing.copy(
                amount = transaction.amount,
                status = STATUS_PENDING,
                updatedAt = System.currentTimeMillis(),
            )
            update(updated)
        } else {
            insert(transaction)
        }

        return true
    }

    @Query("SELECT * FROM transactions")
    suspend fun getAll(): List<TransactionEntity>

    @Query("SELECT * FROM transactions")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun observeById(id: String): Flow<TransactionEntity?>

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}
