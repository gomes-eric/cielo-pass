package com.cielo.cielopass.features.checkout.presentation

import com.cielo.cielopass.core.event.domain.model.Event

data class CheckoutState(
    val isLoading: Boolean = true,
    val isProcessingPayment: Boolean = false,
    val event: Event? = null,
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val document: String = "",
    val documentError: String? = null,
    val quantity: Int = 1,
    val quantityError: String? = null,
    val error: String? = null,
) {
    val unitPrice: Double
        get() = event?.price ?: 0.0

    val totalPrice: Double
        get() = unitPrice * quantity

    val isNameValid: Boolean
        get() = NAME_REGEX.matches(name.trim())

    val isEmailValid: Boolean
        get() = EMAIL_REGEX.matches(email.trim())

    val isDocumentValid: Boolean
        get() = validateDocument(document.trim())

    val isQuantityValid: Boolean
        get() = event != null && quantity > 0 && quantity <= event.availableTickets

    val isFormValid: Boolean
        get() = isNameValid && isEmailValid && isDocumentValid && isQuantityValid && !isLoading && !isProcessingPayment

    companion object {
        val NAME_REGEX = Regex("^[a-zA-ZÀ-ÿ]{2,}(?:\\s+[a-zA-ZÀ-ÿ]{2,})+$")
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        val DOCUMENT_REGEX = Regex("^(\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}|\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2})$")

        fun validateDocument(doc: String): Boolean {
            val clean = doc.replace(Regex("[^0-9]"), "")

            return (clean.length == 11 || clean.length == 14) && DOCUMENT_REGEX.matches(doc)
        }
    }
}

sealed interface CheckoutEvent {
    data class LoadEvent(
        val eventId: String,
    ) : CheckoutEvent

    data class NameChanged(
        val name: String,
    ) : CheckoutEvent

    data class EmailChanged(
        val email: String,
    ) : CheckoutEvent

    data class DocumentChanged(
        val document: String,
    ) : CheckoutEvent

    data class QuantityChanged(
        val quantity: Int,
    ) : CheckoutEvent

    data object ProcessPayment : CheckoutEvent

    data object DismissError : CheckoutEvent
}

sealed interface CheckoutEffect {
    data class ShowToast(
        val message: String,
    ) : CheckoutEffect

    data class NavigateToPaymentResult(
        val status: String,
        val transactionId: String?,
        val errorMessage: String?,
    ) : CheckoutEffect

    data object NavigateBack : CheckoutEffect
}
