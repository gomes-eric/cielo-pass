package com.cielo.cielopass.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Splash : NavKey

@Serializable
data object EventList : NavKey

@Serializable
data class EventDetails(
    val eventId: String,
) : NavKey

@Serializable
data class CheckoutRoute(
    val eventId: String,
) : NavKey

@Serializable
data class PaymentResultRoute(
    val transactionId: String? = null,
    val status: String,
    val errorMessage: String? = null,
    val amount: Long = 0L,
) : NavKey
