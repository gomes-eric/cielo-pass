package com.cielo.cielopass.features.checkout.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cielo.cielopass.core.cielo.domain.model.CieloItem
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult.ActiveTransactionExists
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult.Error
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult.Success
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloPaymentUseCase
import com.cielo.cielopass.core.constants.CheckoutConstants
import com.cielo.cielopass.core.constants.CheckoutConstants.MSG_ACTIVE_TRANSACTION_EXISTS
import com.cielo.cielopass.core.constants.CheckoutConstants.MSG_ACTIVE_TRANSACTION_TOAST
import com.cielo.cielopass.core.constants.CheckoutConstants.MSG_EVENT_NOT_FOUND
import com.cielo.cielopass.core.constants.CheckoutConstants.MSG_INVALID_DOCUMENT_ERROR
import com.cielo.cielopass.core.constants.CheckoutConstants.MSG_INVALID_EMAIL_ERROR
import com.cielo.cielopass.core.constants.CheckoutConstants.MSG_INVALID_NAME_ERROR
import com.cielo.cielopass.core.constants.CheckoutConstants.MSG_STARTING_CIELO_PAYMENT
import com.cielo.cielopass.core.event.domain.repository.EventRepository
import com.cielo.cielopass.features.checkout.presentation.CheckoutEffect.ShowToast
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.DismissError
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.DocumentChanged
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.EmailChanged
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.LoadEvent
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.NameChanged
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.ProcessPayment
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.QuantityChanged
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

class CheckoutViewModel(
    private val eventRepository: EventRepository,
    private val launchCieloPaymentUseCase: LaunchCieloPaymentUseCase,
    savedStateHandle: SavedStateHandle? = null,
) : ViewModel() {
    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    private val _effect = Channel<CheckoutEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var currentEventId: String? = savedStateHandle?.get<String>("eventId")

    init {
        currentEventId?.let { loadEvent(it) }
    }

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is LoadEvent -> handleLoadEvent(event.eventId)
            is NameChanged -> handleNameChanged(event.name)
            is EmailChanged -> handleEmailChanged(event.email)
            is DocumentChanged -> handleDocumentChanged(event.document)
            is QuantityChanged -> handleQuantityChanged(event.quantity)
            is ProcessPayment -> handleProcessPayment()
            is DismissError -> handleDismissError()
        }
    }

    private fun handleLoadEvent(eventId: String) {
        loadEvent(eventId)
    }

    private fun handleNameChanged(name: String) {
        _state.update { currentState ->
            val valid = CheckoutState.NAME_REGEX.matches(name.trim())

            currentState.copy(
                name = name,
                nameError = if (name.isBlank() || valid) null else MSG_INVALID_NAME_ERROR,
            )
        }
    }

    private fun handleEmailChanged(email: String) {
        _state.update { currentState ->
            val valid = CheckoutState.EMAIL_REGEX.matches(email.trim())

            currentState.copy(
                email = email,
                emailError = if (email.isBlank() || valid) null else MSG_INVALID_EMAIL_ERROR,
            )
        }
    }

    private fun handleDocumentChanged(doc: String) {
        _state.update { currentState ->
            val valid = CheckoutState.validateDocument(doc.trim())

            currentState.copy(
                document = doc,
                documentError = if (doc.isBlank() || valid) null else MSG_INVALID_DOCUMENT_ERROR,
            )
        }
    }

    private fun handleQuantityChanged(qty: Int) {
        val available = _state.value.event?.availableTickets ?: 0

        _state.update { currentState ->
            val valid = qty in 1..available

            currentState.copy(
                quantity = qty,
                quantityError = if (valid) null else CheckoutConstants.formatQuantityError(available),
            )
        }
    }

    private fun handleProcessPayment() {
        val currentState = _state.value
        val currentEvent = currentState.event ?: return

        if (!currentState.isFormValid) return

        viewModelScope.launch {
            _state.update { state -> state.copy(isProcessingPayment = true) }

            val totalCents = (currentState.totalPrice * 100).roundToLong()
            val unitPriceCents = (currentEvent.price * 100).roundToLong()

            val request = CieloPaymentRequest(
                amount = totalCents,
                items = listOf(
                    CieloItem(
                        name = "${currentEvent.title} (${currentState.quantity}x)",
                        quantity = currentState.quantity,
                        unitPrice = unitPriceCents,
                    ),
                ),
                eventId = currentEvent.id,
                quantity = currentState.quantity,
            )

            when (val result = launchCieloPaymentUseCase(request)) {
                is Success -> {
                    _effect.send(ShowToast(MSG_STARTING_CIELO_PAYMENT))
                    _state.update { state -> state.copy(isProcessingPayment = false) }
                }

                is ActiveTransactionExists -> {
                    _state.update { state ->
                        state.copy(
                            isProcessingPayment = false,
                            error = MSG_ACTIVE_TRANSACTION_EXISTS,
                        )
                    }

                    _effect.send(ShowToast(MSG_ACTIVE_TRANSACTION_TOAST))
                }

                is Error -> {
                    _state.update { state ->
                        state.copy(
                            isProcessingPayment = false,
                            error = result.message,
                        )
                    }

                    _effect.send(ShowToast(result.message))
                }
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

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true, error = null)
            }

            val loadedEvent = eventRepository.getById(eventId)

            if (loadedEvent == null) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = MSG_EVENT_NOT_FOUND,
                    )
                }
            } else {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        event = loadedEvent,
                        quantity = 1.coerceAtMost(loadedEvent.availableTickets.coerceAtLeast(1)),
                    )
                }
            }
        }
    }
}
