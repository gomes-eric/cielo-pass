package com.cielo.cielopass.core.cielo.domain.model

sealed interface LaunchPaymentResult {
    data object Success : LaunchPaymentResult

    data class ActiveTransactionExists(
        val activeTransactionId: String,
    ) : LaunchPaymentResult

    data class Error(
        val message: String,
    ) : LaunchPaymentResult
}
