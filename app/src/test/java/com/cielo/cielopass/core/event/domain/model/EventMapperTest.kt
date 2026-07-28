package com.cielo.cielopass.core.event.domain.model

import com.cielo.cielopass.core.database.entity.EventEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class EventMapperTest {
    @Test
    fun `given EventEntity when mapped toDomain then return matching Event domain model`() {
        // GIVEN
        val entity = EventEntity(
            id = "e123",
            title = "Rock Fest",
            description = "Great concert",
            date = "2026-12-25",
            venue = "Stadium",
            price = 200.0,
            totalTickets = 500,
            availableTickets = 250,
            imageUrl = "https://example.com/img.jpg",
            createdAt = 1000L,
            updatedAt = 2000L,
        )

        // WHEN
        val domain = entity.toDomain()

        // THEN
        assertEquals("e123", domain.id)
        assertEquals("Rock Fest", domain.title)
        assertEquals("Great concert", domain.description)
        assertEquals("2026-12-25", domain.date)
        assertEquals("Stadium", domain.venue)
        assertEquals(200.0, domain.price, 0.001)
        assertEquals(500, domain.totalTickets)
        assertEquals(250, domain.availableTickets)
        assertEquals("https://example.com/img.jpg", domain.imageUrl)
        assertEquals(1000L, domain.createdAt)
        assertEquals(2000L, domain.updatedAt)
    }

    @Test
    fun `given Event domain model when mapped toEntity then return matching EventEntity`() {
        // GIVEN
        val domain = Event(
            id = "e123",
            title = "Rock Fest",
            description = "Great concert",
            date = "2026-12-25",
            venue = "Stadium",
            price = 200.0,
            totalTickets = 500,
            availableTickets = 250,
            imageUrl = "https://example.com/img.jpg",
            createdAt = 1000L,
            updatedAt = 2000L,
        )

        // WHEN
        val entity = domain.toEntity()

        // THEN
        assertEquals("e123", entity.id)
        assertEquals("Rock Fest", entity.title)
        assertEquals("Great concert", entity.description)
        assertEquals("2026-12-25", entity.date)
        assertEquals("Stadium", entity.venue)
        assertEquals(200.0, entity.price, 0.001)
        assertEquals(500, entity.totalTickets)
        assertEquals(250, entity.availableTickets)
        assertEquals("https://example.com/img.jpg", entity.imageUrl)
        assertEquals(1000L, entity.createdAt)
        assertEquals(2000L, entity.updatedAt)
    }
}
