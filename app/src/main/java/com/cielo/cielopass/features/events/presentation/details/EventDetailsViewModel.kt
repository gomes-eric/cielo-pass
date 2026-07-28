package com.cielo.cielopass.features.events.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cielo.cielopass.core.cielo.domain.model.CieloItem
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult.ActiveTransactionExists
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult.Error
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult.Success
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
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEffect.NavigateBack
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEffect.ShowToast
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.BuyTicket
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.ConfirmDelete
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.DismissDeleteConfirm
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.DismissEditDialog
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.DismissError
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.LoadEvent
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.OpenDeleteConfirm
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.OpenEditDialog
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.UpdateEvent
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
            is LoadEvent -> handleLoadEvent(event.eventId)
            is BuyTicket -> handleBuyTicket()
            is OpenDeleteConfirm -> handleOpenDeleteConfirm()
            is DismissDeleteConfirm -> handleDismissDeleteConfirm()
            is ConfirmDelete -> handleConfirmDelete()
            is OpenEditDialog -> handleOpenEditDialog()
            is DismissEditDialog -> handleDismissEditDialog()
            is UpdateEvent -> handleUpdateEvent(event)
            is DismissError -> handleDismissError()
        }
    }

    private fun handleLoadEvent(eventId: String) {
        loadEvent(eventId)
    }

    private fun handleBuyTicket() {
        val currentEvent = _state.value.event ?: return

        viewModelScope.launch {
            if (currentEvent.availableTickets <= 0) {
                _effect.send(ShowToast(MSG_TICKETS_SOLD_OUT))
                return@launch
            }

            val amountInCents = (currentEvent.price * 100).roundToLong()
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
                    eventId = currentEvent.id,
                    quantity = 1,
                ),
            )

            when (result) {
                is Success -> {
                    _effect.send(ShowToast(MSG_STARTING_CIELO_PAYMENT))
                }

                is ActiveTransactionExists -> {
                    _effect.send(ShowToast(MSG_ACTIVE_TRANSACTION_EXISTS))
                }

                is Error -> {
                    _effect.send(ShowToast(result.message))
                }
            }
        }
    }

    private fun handleOpenDeleteConfirm() {
        _state.update { currentState ->
            currentState.copy(isDeleteConfirmDialogOpen = true)
        }
    }

    private fun handleDismissDeleteConfirm() {
        _state.update { currentState ->
            currentState.copy(isDeleteConfirmDialogOpen = false)
        }
    }

    private fun handleConfirmDelete() {
        val eventId = currentEventId ?: return

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isDeleting = true, isDeleteConfirmDialogOpen = false)
            }

            try {
                eventRepository.deleteById(eventId)
                observeJob?.cancel()

                _effect.send(ShowToast(MSG_EVENT_DELETED_SUCCESS))
                _effect.send(NavigateBack)
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        isDeleting = false,
                        error = e.localizedMessage ?: MSG_ERROR_DELETE_EVENT,
                    )
                }
            }
        }
    }

    private fun handleOpenEditDialog() {
        _state.update { currentState ->
            currentState.copy(isEditDialogOpen = true)
        }
    }

    private fun handleDismissEditDialog() {
        _state.update { currentState ->
            currentState.copy(isEditDialogOpen = false)
        }
    }

    private fun handleUpdateEvent(event: UpdateEvent) {
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

                _state.update { currentState ->
                    currentState.copy(isEditDialogOpen = false)
                }

                _effect.send(ShowToast(MSG_EVENT_UPDATED_SUCCESS))
            } catch (e: Exception) {
                _effect.send(ShowToast(e.localizedMessage ?: MSG_ERROR_UPDATE_EVENT))
            }
        }
    }

    private fun handleDismissError() {
        _state.update { currentState ->
            currentState.copy(error = null)
        }
    }

    private fun loadEvent(eventId: String) {
        currentEventId = eventId
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true, isDeleting = false, event = null, error = null)
            }

            eventRepository
                .observeById(eventId)
                .catch { exception ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = exception.localizedMessage ?: MSG_ERROR_LOAD_EVENT_DETAILS,
                        )
                    }
                }.collect { event ->
                    if (_state.value.isDeleting) return@collect

                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            event = event,
                            error = if (event == null) MSG_EVENT_NOT_FOUND else null,
                        )
                    }
                }
        }
    }
}
