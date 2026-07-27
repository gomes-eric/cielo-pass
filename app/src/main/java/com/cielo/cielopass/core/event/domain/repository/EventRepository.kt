package com.cielo.cielopass.core.event.domain.repository

import com.cielo.cielopass.core.event.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun insert(event: Event)

    suspend fun insert(events: List<Event>)

    suspend fun update(event: Event)

    suspend fun getById(id: String): Event?

    suspend fun getAll(): List<Event>

    fun observeAll(): Flow<List<Event>>

    fun observeById(id: String): Flow<Event?>

    suspend fun deleteById(id: String)

    suspend fun deleteAll()
}
