package com.cielo.cielopass.core.event.data.repository

import com.cielo.cielopass.core.database.dao.EventDAO
import com.cielo.cielopass.core.database.entity.EventEntity
import com.cielo.cielopass.core.event.domain.model.Event
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class EventRepositoryImplTest {
    private lateinit var dao: EventDAO
    private lateinit var repository: EventRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk()
        repository = EventRepositoryImpl(dao)
    }

    @Test
    fun `given event domain model when insert called then map and pass to dao`() =
        runTest {
            // GIVEN
            val event = Event(
                id = "e1",
                title = "Fest",
                description = "Desc",
                date = "2026-10-10",
                venue = "Stage",
                price = 50.0,
                totalTickets = 10,
                availableTickets = 5,
            )
            coEvery { dao.insert(any<EventEntity>()) } just runs

            // WHEN
            repository.insert(event)

            // THEN
            coVerify(exactly = 1) { dao.insert(any<EventEntity>()) }
        }

    @Test
    fun `given event id when getById called then return mapped domain event`() =
        runTest {
            // GIVEN
            val entity = EventEntity(
                id = "e1",
                title = "Fest",
                description = "Desc",
                date = "2026-10-10",
                venue = "Stage",
                price = 50.0,
                totalTickets = 10,
                availableTickets = 5,
            )
            coEvery { dao.getById("e1") } returns entity

            // WHEN
            val result = repository.getById("e1")

            // THEN
            assertEquals("e1", result?.id)
            assertEquals("Fest", result?.title)
        }

    @Test
    fun `given non-existent event id when getById called then return null`() =
        runTest {
            // GIVEN
            coEvery { dao.getById("missing") } returns null

            // WHEN
            val result = repository.getById("missing")

            // THEN
            assertNull(result)
        }

    @Test
    fun `given observeAll flow in dao when collected then return mapped domain list`() =
        runTest {
            // GIVEN
            val entities = listOf(
                EventEntity(
                    id = "e1",
                    title = "Fest",
                    description = "Desc",
                    date = "2026-10-10",
                    venue = "Stage",
                    price = 50.0,
                    totalTickets = 10,
                    availableTickets = 5,
                ),
            )
            coEvery { dao.observeAll() } returns flowOf(entities)

            // WHEN
            val events = repository.observeAll().first()

            // THEN
            assertEquals(1, events.size)
            assertEquals("e1", events[0].id)
        }

    @Test
    fun `given event id when deleteById called then invoke dao deleteById`() =
        runTest {
            // GIVEN
            coEvery { dao.deleteById("e1") } just runs

            // WHEN
            repository.deleteById("e1")

            // THEN
            coVerify(exactly = 1) { dao.deleteById("e1") }
        }
}
