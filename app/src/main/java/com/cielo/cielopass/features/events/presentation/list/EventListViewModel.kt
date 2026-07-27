package com.cielo.cielopass.features.events.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cielo.cielopass.core.constants.EventConstants.DEFAULT_EVENT_DATE
import com.cielo.cielopass.core.constants.EventConstants.DEFAULT_EVENT_DESCRIPTION
import com.cielo.cielopass.core.constants.EventConstants.DEFAULT_EVENT_TITLE
import com.cielo.cielopass.core.constants.EventConstants.DEFAULT_EVENT_VENUE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_1_DATE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_1_DESCRIPTION
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_1_TITLE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_1_VENUE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_2_DATE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_2_DESCRIPTION
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_2_TITLE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_2_VENUE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_3_DATE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_3_DESCRIPTION
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_3_TITLE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_3_VENUE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_4_DATE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_4_DESCRIPTION
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_4_TITLE
import com.cielo.cielopass.core.constants.EventConstants.MOCK_EVENT_4_VENUE
import com.cielo.cielopass.core.constants.EventConstants.MSG_ERROR_CLEAR_EVENTS
import com.cielo.cielopass.core.constants.EventConstants.MSG_ERROR_CREATE_EVENT
import com.cielo.cielopass.core.constants.EventConstants.MSG_ERROR_OBSERVE_EVENTS
import com.cielo.cielopass.core.constants.EventConstants.MSG_ERROR_SEED_MOCK_EVENTS
import com.cielo.cielopass.core.constants.EventConstants.MSG_EVENTS_CLEARED
import com.cielo.cielopass.core.constants.EventConstants.MSG_EVENT_CREATED_SUCCESS
import com.cielo.cielopass.core.constants.EventConstants.MSG_MOCK_EVENTS_SEEDED
import com.cielo.cielopass.core.event.domain.model.Event
import com.cielo.cielopass.core.event.domain.repository.EventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class EventListViewModel(
    private val eventRepository: EventRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(EventListUiState(isLoading = true))
    val state: StateFlow<EventListUiState> = _state.asStateFlow()

    private val _effect = Channel<EventListEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var observeJob: Job? = null

    init {
        observeEvents()
    }

    fun onEvent(event: EventListEvent) {
        when (event) {
            is EventListEvent.LoadEvents -> observeEvents()
            is EventListEvent.SelectEvent -> {
                viewModelScope.launch {
                    _effect.send(EventListEffect.NavigateToDetails(event.eventId))
                }
            }
            is EventListEvent.ToggleSpeedDial -> {
                _state.update { it.copy(isSpeedDialExpanded = !it.isSpeedDialExpanded) }
            }
            is EventListEvent.OpenAddDialog -> {
                _state.update { it.copy(isAddDialogOpen = true, isSpeedDialExpanded = false) }
            }
            is EventListEvent.DismissAddDialog -> {
                _state.update { it.copy(isAddDialogOpen = false) }
            }
            is EventListEvent.AddEvent -> createEvent(event)
            is EventListEvent.SeedMockEvents -> seedMockEvents()
            is EventListEvent.ClearEvents -> clearEvents()
            is EventListEvent.DismissError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun observeEvents() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            eventRepository
                .observeAll()
                .catch { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.localizedMessage ?: MSG_ERROR_OBSERVE_EVENTS,
                        )
                    }
                }.collect { events ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            events = events,
                            error = null,
                        )
                    }
                }
        }
    }

    private fun createEvent(event: EventListEvent.AddEvent) {
        viewModelScope.launch {
            try {
                val newEvent = Event(
                    id = UUID.randomUUID().toString(),
                    title = event.title.ifBlank { DEFAULT_EVENT_TITLE },
                    description = event.description.ifBlank { DEFAULT_EVENT_DESCRIPTION },
                    date = event.date.ifBlank { DEFAULT_EVENT_DATE },
                    venue = event.venue.ifBlank { DEFAULT_EVENT_VENUE },
                    price = event.price.coerceAtLeast(0.0),
                    totalTickets = event.totalTickets.coerceAtLeast(1),
                    availableTickets = event.availableTickets.coerceIn(0, event.totalTickets.coerceAtLeast(1)),
                    imageUrl = event.imageUrl?.takeIf { it.isNotBlank() },
                )
                eventRepository.insert(newEvent)
                _state.update { it.copy(isAddDialogOpen = false) }
                _effect.send(EventListEffect.ShowToast(MSG_EVENT_CREATED_SUCCESS))
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.localizedMessage ?: MSG_ERROR_CREATE_EVENT)
                }
            }
        }
    }

    private fun seedMockEvents() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isSpeedDialExpanded = false) }
                val mockEvents = listOf(
                    Event(
                        id = UUID.randomUUID().toString(),
                        title = MOCK_EVENT_1_TITLE,
                        description = MOCK_EVENT_1_DESCRIPTION,
                        date = MOCK_EVENT_1_DATE,
                        venue = MOCK_EVENT_1_VENUE,
                        price = 250.00,
                        totalTickets = 500,
                        availableTickets = 42,
                    ),
                    Event(
                        id = UUID.randomUUID().toString(),
                        title = MOCK_EVENT_2_TITLE,
                        description = MOCK_EVENT_2_DESCRIPTION,
                        date = MOCK_EVENT_2_DATE,
                        venue = MOCK_EVENT_2_VENUE,
                        price = 120.00,
                        totalTickets = 150,
                        availableTickets = 15,
                    ),
                    Event(
                        id = UUID.randomUUID().toString(),
                        title = MOCK_EVENT_3_TITLE,
                        description = MOCK_EVENT_3_DESCRIPTION,
                        date = MOCK_EVENT_3_DATE,
                        venue = MOCK_EVENT_3_VENUE,
                        price = 350.00,
                        totalTickets = 300,
                        availableTickets = 110,
                    ),
                    Event(
                        id = UUID.randomUUID().toString(),
                        title = MOCK_EVENT_4_TITLE,
                        description = MOCK_EVENT_4_DESCRIPTION,
                        date = MOCK_EVENT_4_DATE,
                        venue = MOCK_EVENT_4_VENUE,
                        price = 80.00,
                        totalTickets = 200,
                        availableTickets = 8,
                    ),
                )
                eventRepository.insert(mockEvents)
                _effect.send(EventListEffect.ShowToast(MSG_MOCK_EVENTS_SEEDED))
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.localizedMessage ?: MSG_ERROR_SEED_MOCK_EVENTS)
                }
            }
        }
    }

    private fun clearEvents() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isSpeedDialExpanded = false) }
                eventRepository.deleteAll()
                _effect.send(EventListEffect.ShowToast(MSG_EVENTS_CLEARED))
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.localizedMessage ?: MSG_ERROR_CLEAR_EVENTS)
                }
            }
        }
    }
}
