package com.cielo.cielopass.core.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Payment
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.TerminalError
import com.cielo.cielopass.core.cielo.domain.model.CieloItem
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Approved
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Cancelled
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Failed
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloPaymentUseCase
import com.cielo.cielopass.core.cielo.presentation.CieloDeeplinkManager
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_APPROVED
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_CANCELLED
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_FAILED
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_UNKNOWN
import com.cielo.cielopass.core.constants.NavigationConstants.MSG_PAYMENT_APPROVED_SUCCESS
import com.cielo.cielopass.core.constants.NavigationConstants.MSG_PAYMENT_CANCELLED_USER
import com.cielo.cielopass.core.constants.NavigationConstants.MSG_PAYMENT_PROCESS_FAILED
import com.cielo.cielopass.core.constants.NavigationConstants.UNKNOWN_ORDER_ID
import com.cielo.cielopass.features.splash.presentation.SplashScreen
import org.koin.compose.koinInject
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Unknown as UnknownResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Unknown as UnknownResult

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    cieloDeeplinkManager: CieloDeeplinkManager = koinInject(),
    launchCieloPaymentUseCase: LaunchCieloPaymentUseCase = koinInject(),
) {
    val backStack = remember { mutableStateListOf<NavKey>(Splash) }

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
                            backStack.add(Events)
                        }
                    }
                }

                is Events -> {
                    NavEntry(key) {
                        Text("Rota de eventos")

                        LaunchedEffect(Unit) {
                            launchCieloPaymentUseCase(
                                CieloPaymentRequest(
                                    amount = 100L,
                                    items = listOf(
                                        CieloItem(
                                            unitPrice = 100L,
                                        ),
                                    ),
                                ),
                            )
                        }
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
