package com.cielo.cielopass.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cielo.cielopass.core.database.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Update
    suspend fun update(event: EventEntity)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getById(id: String): EventEntity?

    @Query("SELECT * FROM events")
    suspend fun getAll(): List<EventEntity>

    @Query("SELECT * FROM events")
    fun observeAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun observeById(id: String): Flow<EventEntity?>

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM events")
    suspend fun deleteAll()
}
