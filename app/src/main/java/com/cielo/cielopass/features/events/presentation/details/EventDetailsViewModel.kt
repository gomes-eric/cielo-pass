package com.cielo.cielopass.features.events.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cielo.cielopass.core.cielo.domain.model.CieloItem
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloPaymentUseCase
import com.cielo.cielopass.core.constants.EventConstants.MSG_ACTIVE_TRANSACTION_EXISTS
import com.cielo.cielopass.core.constants.EventConstants.MSG_ERROR_DELETE_EVENT
import com.cielo.cielopass.core.constants.EventConstants.MSG_ERROR_LOAD_EVENT_DETAILS
import com.cielo.cielopass.core.constants.EventConstants.MSG_ERROR_UPDATE_EVENT
import com.cielo.cielopass.core.constants.EventConstants.MSG_EVENT_DELETED_SUCCESS
import com.cielo.cielopass.core.constants.EventConstants.MSG_EVENT_NOT_FOUND
import com.cielo.cielopass.core.constants.EventConstants.MSG_EVENT_UPDATED_SUCCESS
import com.cielo.cielopass.core.constants.EventConstants.MSG_STARTING_CIELO_PAYMENT
import com.cielo.cielopass.core.constants.EventConstants.MSG_TICKETS_SOLD_OUT
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
import kotlin.math.roundToLong

class EventDetailsViewModel(
    private val eventRepository: EventRepository,
    private val launchCieloPaymentUseCase: LaunchCieloPaymentUseCase,
    savedStateHandle: SavedStateHandle? = null,
) : ViewModel() {
    private val _state = MutableStateFlow(EventDetailsUiState(isLoading = true))
    val state: StateFlow<EventDetailsUiState> = _state.asStateFlow()

    private val _effect = Channel<EventDetailsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var currentEventId: String? = savedStateHandle?.get<String>("eventId")
    private var observeJob: Job? = null

    init {
        currentEventId?.let { loadEvent(it) }
    }

    fun onEvent(event: EventDetailsEvent) {
        when (event) {
            is EventDetailsEvent.LoadEvent -> {
                loadEvent(event.eventId)
            }

            is EventDetailsEvent.BuyTicket -> {
                processBuyTicket()
            }

            is EventDetailsEvent.OpenDeleteConfirm -> {
                _state.update { it.copy(isDeleteConfirmDialogOpen = true) }
            }

            is EventDetailsEvent.DismissDeleteConfirm -> {
                _state.update { it.copy(isDeleteConfirmDialogOpen = false) }
            }

            is EventDetailsEvent.ConfirmDelete -> {
                deleteEvent()
            }

            is EventDetailsEvent.OpenEditDialog -> {
                _state.update { it.copy(isEditDialogOpen = true) }
            }

            is EventDetailsEvent.DismissEditDialog -> {
                _state.update { it.copy(isEditDialogOpen = false) }
            }

            is EventDetailsEvent.UpdateEvent -> {
                updateEvent(event)
            }

            is EventDetailsEvent.DismissError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadEvent(eventId: String) {
        currentEventId = eventId
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isDeleting = false, event = null, error = null) }
            eventRepository
                .observeById(eventId)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.localizedMessage ?: MSG_ERROR_LOAD_EVENT_DETAILS,
                        )
                    }
                }.collect { event ->
                    if (_state.value.isDeleting) return@collect
                    _state.update {
                        it.copy(
                            isLoading = false,
                            event = event,
                            error = if (event == null) MSG_EVENT_NOT_FOUND else null,
                        )
                    }
                }
        }
    }

    private fun processBuyTicket() {
        val currentEvent = _state.value.event ?: return
        if (currentEvent.availableTickets <= 0) {
            viewModelScope.launch {
                _effect.send(EventDetailsEffect.ShowToast(MSG_TICKETS_SOLD_OUT))
            }
            return
        }

        val amountInCents = (currentEvent.price * 100).roundToLong()
        viewModelScope.launch {
            val result = launchCieloPaymentUseCase(
                CieloPaymentRequest(
                    amount = amountInCents,
                    items = listOf(
                        CieloItem(
                            name = currentEvent.title,
                            quantity = 1,
                            unitPrice = amountInCents,
                        ),
                    ),
                ),
            )
            when (result) {
                is LaunchPaymentResult.Success -> {
                    _effect.send(EventDetailsEffect.ShowToast(MSG_STARTING_CIELO_PAYMENT))
                }

                is LaunchPaymentResult.ActiveTransactionExists -> {
                    _effect.send(
                        EventDetailsEffect.ShowToast(MSG_ACTIVE_TRANSACTION_EXISTS),
                    )
                }

                is LaunchPaymentResult.Error -> {
                    _effect.send(
                        EventDetailsEffect.ShowToast(result.message),
                    )
                }
            }
        }
    }

    private fun deleteEvent() {
        val eventId = currentEventId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, isDeleteConfirmDialogOpen = false) }
            try {
                eventRepository.deleteById(eventId)
                observeJob?.cancel()
                _effect.send(EventDetailsEffect.ShowToast(MSG_EVENT_DELETED_SUCCESS))
                _effect.send(EventDetailsEffect.NavigateBack)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isDeleting = false,
                        error = e.localizedMessage ?: MSG_ERROR_DELETE_EVENT,
                    )
                }
            }
        }
    }

    private fun updateEvent(event: EventDetailsEvent.UpdateEvent) {
        val currentEvent = _state.value.event ?: return
        viewModelScope.launch {
            try {
                val updated = currentEvent.copy(
                    title = event.title.ifBlank { currentEvent.title },
                    description = event.description.ifBlank { currentEvent.description },
                    date = event.date.ifBlank { currentEvent.date },
                    venue = event.venue.ifBlank { currentEvent.venue },
                    price = event.price.coerceAtLeast(0.0),
                    totalTickets = event.totalTickets.coerceAtLeast(1),
                    availableTickets = event.availableTickets.coerceIn(0, event.totalTickets.coerceAtLeast(1)),
                    imageUrl = event.imageUrl?.takeIf { it.isNotBlank() },
                    updatedAt = System.currentTimeMillis(),
                )
                eventRepository.update(updated)
                _state.update { it.copy(isEditDialogOpen = false) }
                _effect.send(EventDetailsEffect.ShowToast(MSG_EVENT_UPDATED_SUCCESS))
            } catch (e: Exception) {
                _effect.send(
                    EventDetailsEffect.ShowToast(e.localizedMessage ?: MSG_ERROR_UPDATE_EVENT),
                )
            }
        }
    }
}
