package com.cielo.cielopass.features.payment.presentation

data class PaymentResultState(
    val isLoading: Boolean = false,
    val status: String = "",
    val transactionId: String? = null,
    val errorMessage: String? = null,
    val amount: Long = 0L,
) {
    val isApproved: Boolean
        get() = status.equals("APPROVED", ignoreCase = true) ||
            status.equals("SUCCESS", ignoreCase = true) ||
            status.equals("0", ignoreCase = true)

    val isCancelled: Boolean
        get() = status.equals("CANCELLED", ignoreCase = true) ||
            status.equals("1", ignoreCase = true)

    val isFailed: Boolean
        get() = status.equals("FAILED", ignoreCase = true) ||
            status.equals("ERROR", ignoreCase = true) ||
            status.equals("2", ignoreCase = true) ||
            status.equals("3", ignoreCase = true) ||
            status.equals("4", ignoreCase = true)

    val isUnknown: Boolean
        get() = !isApproved && !isCancelled && !isFailed
}

sealed interface PaymentResultEvent {
    data class Init(
        val status: String,
        val transactionId: String?,
        val errorMessage: String?,
        val amount: Long = 0L,
        val reference: String? = null,
    ) : PaymentResultEvent

    data object BackToHome : PaymentResultEvent

    data object RetryPayment : PaymentResultEvent

    data object CheckPendingStatus : PaymentResultEvent
}

sealed interface PaymentResultEffect {
    data object NavigateToHome : PaymentResultEffect

    data object NavigateToCheckout : PaymentResultEffect

    data class ShowToast(
        val message: String,
    ) : PaymentResultEffect
}
