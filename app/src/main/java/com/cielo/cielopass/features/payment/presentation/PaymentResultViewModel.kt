package com.cielo.cielopass.features.payment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_APPROVED
import com.cielo.cielopass.core.constants.PaymentConstants.MSG_NO_PENDING_TRANSACTION
import com.cielo.cielopass.core.event.domain.repository.EventRepository
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository
import com.cielo.cielopass.features.payment.presentation.PaymentResultEffect.NavigateToCheckout
import com.cielo.cielopass.features.payment.presentation.PaymentResultEffect.NavigateToHome
import com.cielo.cielopass.features.payment.presentation.PaymentResultEffect.ShowToast
import com.cielo.cielopass.features.payment.presentation.PaymentResultEvent.BackToHome
import com.cielo.cielopass.features.payment.presentation.PaymentResultEvent.CheckPendingStatus
import com.cielo.cielopass.features.payment.presentation.PaymentResultEvent.Init
import com.cielo.cielopass.features.payment.presentation.PaymentResultEvent.RetryPayment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentResultViewModel(
    private val transactionRepository: TransactionRepository,
    private val eventRepository: EventRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(PaymentResultState())
    val state: StateFlow<PaymentResultState> = _state.asStateFlow()

    private val _effect = Channel<PaymentResultEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: PaymentResultEvent) {
        when (event) {
            is Init -> handleInit(event)
            is BackToHome -> handleBackToHome()
            is RetryPayment -> handleRetryPayment()
            is CheckPendingStatus -> handleCheckPendingStatus()
        }
    }

    private fun handleInit(event: Init) {
        init(
            status = event.status,
            transactionId = event.transactionId,
            errorMessage = event.errorMessage,
            amount = event.amount,
            reference = event.reference,
        )
    }

    private fun handleBackToHome() {
        viewModelScope.launch {
            _effect.send(NavigateToHome)
        }
    }

    private fun handleRetryPayment() {
        viewModelScope.launch {
            _effect.send(NavigateToCheckout)
        }
    }

    private fun handleCheckPendingStatus() {
        checkPendingStatus()
    }

    private fun init(
        status: String,
        transactionId: String?,
        errorMessage: String?,
        amount: Long,
        reference: String?,
    ) {
        viewModelScope.launch {
            val isApproved = status.equals(STATUS_APPROVED, ignoreCase = true) ||
                status.equals("SUCCESS", ignoreCase = true) ||
                status.equals("0", ignoreCase = true)

            val transaction = (reference?.let { transactionRepository.getById(it) })
                ?: (transactionId?.let { transactionRepository.getById(it) })
                ?: transactionRepository.getPending()
                ?: transactionRepository.getAll().lastOrNull()

            val effectiveTxId = transaction?.id ?: transactionId ?: reference
            val effectiveReference = reference ?: transaction?.id ?: effectiveTxId

            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    status = status,
                    transactionId = effectiveReference,
                    errorMessage = errorMessage ?: transaction?.errorMessage,
                    amount = if (amount > 0) amount else (transaction?.amount ?: 0L),
                )
            }

            if (isApproved) {
                processApprovedPayment(effectiveTxId)
            }
        }
    }

    private suspend fun processApprovedPayment(txId: String?) {
        try {
            val transaction = txId?.let { transactionRepository.getById(it) }
                ?: transactionRepository.getPending()
                ?: transactionRepository.getAll().lastOrNull()

            if (transaction != null && !transaction.inventoryDeducted) {
                val eventId = transaction.eventId
                val event = if (!eventId.isNullOrBlank()) {
                    eventRepository.getById(eventId)
                } else {
                    eventRepository.getAll().firstOrNull()
                }

                if (event != null && event.availableTickets > 0) {
                    val quantityBought = transaction.quantity.coerceAtLeast(1)
                    val updatedTickets = (event.availableTickets - quantityBought).coerceAtLeast(0)

                    eventRepository.update(event.copy(availableTickets = updatedTickets))
                    transactionRepository.update(transaction.copy(inventoryDeducted = true))
                }
            }
        } catch (_: Exception) {
            // Non-blocking catch to ensure UI rendering is preserved
        }
    }

    private fun checkPendingStatus() {
        viewModelScope.launch {
            _state.update { currentState -> currentState.copy(isLoading = true) }

            val pending = transactionRepository.getPending()

            if (pending == null) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = MSG_NO_PENDING_TRANSACTION,
                    )
                }

                _effect.send(ShowToast(MSG_NO_PENDING_TRANSACTION))
            } else {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        status = pending.status,
                        transactionId = pending.id,
                        amount = pending.amount,
                        errorMessage = pending.errorMessage,
                    )
                }
            }
        }
    }
}
