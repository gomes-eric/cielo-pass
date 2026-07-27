package com.cielo.cielopass.core.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Payment
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.TerminalError
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Approved
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Cancelled
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Failed
import com.cielo.cielopass.core.cielo.presentation.CieloDeeplinkManager
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_APPROVED
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_CANCELLED
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_FAILED
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_UNKNOWN
import com.cielo.cielopass.core.constants.NavigationConstants.MSG_PAYMENT_APPROVED_SUCCESS
import com.cielo.cielopass.core.constants.NavigationConstants.MSG_PAYMENT_CANCELLED_USER
import com.cielo.cielopass.core.constants.NavigationConstants.MSG_PAYMENT_PROCESS_FAILED
import com.cielo.cielopass.core.constants.NavigationConstants.UNKNOWN_ORDER_ID
import com.cielo.cielopass.features.events.presentation.details.EventDetailsScreen
import com.cielo.cielopass.features.events.presentation.list.EventListScreen
import com.cielo.cielopass.features.splash.presentation.SplashScreen
import org.koin.compose.koinInject
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Unknown as UnknownResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Unknown as UnknownResult

private fun serializeNavKey(key: NavKey): String =
    when (key) {
        is Splash -> "splash"
        is EventList -> "event_list"
        is EventDetails -> "event_details:${key.eventId}"
        is PaymentResult -> "payment_result:${key.orderId}|${key.status}|${key.message.orEmpty()}|${key.amount}"
        else -> "splash"
    }

private fun deserializeNavKey(value: String): NavKey =
    when {
        value == "splash" -> {
            Splash
        }

        value == "event_list" -> {
            EventList
        }

        value.startsWith("event_details:") -> {
            val eventId = value.removePrefix("event_details:")
            EventDetails(eventId)
        }

        value.startsWith("payment_result:") -> {
            val parts = value.removePrefix("payment_result:").split("|")
            PaymentResult(
                orderId = parts.getOrNull(0).orEmpty(),
                status = parts.getOrNull(1).orEmpty(),
                message = parts.getOrNull(2)?.ifEmpty { null },
                amount = parts.getOrNull(3)?.toLongOrNull() ?: 0L,
            )
        }

        else -> {
            Splash
        }
    }

private val NavKeyListSaver = Saver<SnapshotStateList<NavKey>, ArrayList<String>>(
    save = { list ->
        ArrayList(list.map { serializeNavKey(it) })
    },
    restore = { savedList ->
        val restoredKeys = savedList.map { deserializeNavKey(it) }
        mutableStateListOf<NavKey>().apply { addAll(restoredKeys) }
    },
)

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    cieloDeeplinkManager: CieloDeeplinkManager = koinInject(),
) {
    val backStack = rememberSaveable(saver = NavKeyListSaver) {
        mutableStateListOf(Splash)
    }

    LaunchedEffect(Unit) {
        cieloDeeplinkManager.deeplinkResponse.collect { response ->
            when (response) {
                is Payment -> {
                    val route = when (val result = response.result) {
                        is Approved -> PaymentResult(
                            orderId = result.orderId,
                            status = STATUS_APPROVED,
                            message = MSG_PAYMENT_APPROVED_SUCCESS,
                            amount = result.amount,
                        )

                        is Cancelled -> PaymentResult(
                            orderId = result.orderId,
                            status = STATUS_CANCELLED,
                            message = result.reason ?: MSG_PAYMENT_CANCELLED_USER,
                        )

                        is Failed -> PaymentResult(
                            orderId = result.orderId,
                            status = STATUS_FAILED,
                            message = result.message ?: result.reason ?: MSG_PAYMENT_PROCESS_FAILED,
                        )

                        is UnknownResult -> PaymentResult(
                            orderId = UNKNOWN_ORDER_ID,
                            status = STATUS_UNKNOWN,
                            message = result.error,
                        )
                    }

                    backStack.add(route)
                }

                is TerminalError -> {
                    val route = PaymentResult(
                        orderId = UNKNOWN_ORDER_ID,
                        status = STATUS_FAILED,
                        message = response.message ?: MSG_PAYMENT_PROCESS_FAILED,
                    )

                    backStack.add(route)
                }

                is UnknownResponse -> {
                    // Fallback for unknown deep link schemes
                }

                else -> {
                    // Handle other response types or log as needed
                }
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Splash -> {
                    NavEntry(key) {
                        SplashScreen {
                            backStack.clear()
                            backStack.add(EventList)
                        }
                    }
                }

                is EventList -> {
                    NavEntry(key) {
                        EventListScreen(
                            onNavigateToDetails = { eventId ->
                                backStack.add(EventDetails(eventId = eventId))
                            },
                        )
                    }
                }

                is EventDetails -> {
                    NavEntry(key) {
                        EventDetailsScreen(
                            eventId = key.eventId,
                            onNavigateBack = {
                                backStack.removeLastOrNull()
                            },
                        )
                    }
                }

                is PaymentResult -> {
                    NavEntry(key) {
                        Text("Rota de resultado de pagamento: ${key.orderId}, Status: ${key.status}, Mensagem: ${key.message}")
                    }
                }

                else -> {
                    NavEntry(key) {
                        Text("Rota desconhecida")
                    }
                }
            }
        },
    )
}
