package com.cielo.cielopass.features.events.presentation.details

import com.cielo.cielopass.core.event.domain.model.Event

data class EventDetailsUiState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val error: String? = null,
    val isDeleting: Boolean = false,
    val isEditDialogOpen: Boolean = false,
    val isDeleteConfirmDialogOpen: Boolean = false,
)

sealed interface EventDetailsEvent {
    data class LoadEvent(
        val eventId: String,
    ) : EventDetailsEvent

    data object BuyTicket : EventDetailsEvent

    data object OpenDeleteConfirm : EventDetailsEvent

    data object DismissDeleteConfirm : EventDetailsEvent

    data object ConfirmDelete : EventDetailsEvent

    data object OpenEditDialog : EventDetailsEvent

    data object DismissEditDialog : EventDetailsEvent

    data class UpdateEvent(
        val title: String,
        val description: String,
        val date: String,
        val venue: String,
        val price: Double,
        val totalTickets: Int,
        val availableTickets: Int,
        val imageUrl: String? = null,
    ) : EventDetailsEvent

    data object DismissError : EventDetailsEvent
}

sealed interface EventDetailsEffect {
    data object NavigateBack : EventDetailsEffect

    data class ShowToast(
        val message: String,
    ) : EventDetailsEffect
}
