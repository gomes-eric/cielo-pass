package com.cielo.cielopass.core.event.domain.model

import com.cielo.cielopass.core.database.entity.EventEntity

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val venue: String,
    val price: Double,
    val totalTickets: Int,
    val availableTickets: Int,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

fun EventEntity.toDomain(): Event =
    Event(
        id = id,
        title = title,
        description = description,
        date = date,
        venue = venue,
        price = price,
        totalTickets = totalTickets,
        availableTickets = availableTickets,
        imageUrl = imageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Event.toEntity(): EventEntity =
    EventEntity(
        id = id,
        title = title,
        description = description,
        date = date,
        venue = venue,
        price = price,
        totalTickets = totalTickets,
        availableTickets = availableTickets,
        imageUrl = imageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
