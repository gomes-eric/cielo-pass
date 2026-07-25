package com.cielo.cielopass.core.event.data.repository

import com.cielo.cielopass.core.database.dao.EventDAO
import com.cielo.cielopass.core.database.entity.EventEntity
import com.cielo.cielopass.core.event.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class EventRepositoryImpl(
    private val dao: EventDAO,
) : EventRepository {
    override suspend fun insert(event: EventEntity) = dao.insert(event)

    override suspend fun insert(events: List<EventEntity>) = dao.insertAll(events)

    override suspend fun update(event: EventEntity) = dao.update(event)

    override suspend fun getById(id: String): EventEntity? = dao.getById(id)

    override suspend fun getAll(): List<EventEntity> = dao.getAll()

    override fun observeAll(): Flow<List<EventEntity>> = dao.observeAll()

    override fun observeById(id: String): Flow<EventEntity?> = dao.observeById(id)

    override suspend fun deleteById(id: String) = dao.deleteById(id)

    override suspend fun deleteAll() = dao.deleteAll()
}
