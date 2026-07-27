package com.cielo.cielopass.core.event.domain.repository

import com.cielo.cielopass.core.database.entity.EventEntity
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun insert(event: EventEntity)

    suspend fun insert(events: List<EventEntity>)

    suspend fun update(event: EventEntity)

    suspend fun getById(id: String): EventEntity?

    suspend fun getAll(): List<EventEntity>

    fun observeAll(): Flow<List<EventEntity>>

    fun observeById(id: String): Flow<EventEntity?>

    suspend fun deleteById(id: String)

    suspend fun deleteAll()
}
