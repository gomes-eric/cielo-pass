package com.cielo.cielopass.features.events.presentation.list

import com.cielo.cielopass.core.event.domain.model.Event

data class EventListUiState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val error: String? = null,
    val isAddDialogOpen: Boolean = false,
    val isSpeedDialExpanded: Boolean = false,
)

sealed interface EventListEvent {
    data object LoadEvents : EventListEvent

    data class SelectEvent(
        val eventId: String,
    ) : EventListEvent

    data object ToggleSpeedDial : EventListEvent

    data object OpenAddDialog : EventListEvent

    data object DismissAddDialog : EventListEvent

    data class AddEvent(
        val title: String,
        val description: String,
        val date: String,
        val venue: String,
        val price: Double,
        val totalTickets: Int,
        val availableTickets: Int,
        val imageUrl: String? = null,
    ) : EventListEvent

    data object SeedMockEvents : EventListEvent

    data object ClearEvents : EventListEvent

    data object DismissError : EventListEvent
}

sealed interface EventListEffect {
    data class NavigateToDetails(
        val eventId: String,
    ) : EventListEffect

    data class ShowToast(
        val message: String,
    ) : EventListEffect
}
