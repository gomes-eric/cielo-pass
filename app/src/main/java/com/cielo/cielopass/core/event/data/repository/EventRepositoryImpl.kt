package com.cielo.cielopass.core.event.data.repository

import com.cielo.cielopass.core.database.dao.EventDAO
import com.cielo.cielopass.core.event.domain.model.Event
import com.cielo.cielopass.core.event.domain.model.toDomain
import com.cielo.cielopass.core.event.domain.model.toEntity
import com.cielo.cielopass.core.event.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepositoryImpl(
    private val dao: EventDAO,
) : EventRepository {
    override suspend fun insert(event: Event) = dao.insert(event.toEntity())

    override suspend fun insert(events: List<Event>) = dao.insertAll(events.map { it.toEntity() })

    override suspend fun update(event: Event) = dao.update(event.toEntity())

    override suspend fun getById(id: String): Event? = dao.getById(id)?.toDomain()

    override suspend fun getAll(): List<Event> = dao.getAll().map { it.toDomain() }

    override fun observeAll(): Flow<List<Event>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Event?> = dao.observeById(id).map { it?.toDomain() }

    override suspend fun deleteById(id: String) = dao.deleteById(id)

    override suspend fun deleteAll() = dao.deleteAll()
}
