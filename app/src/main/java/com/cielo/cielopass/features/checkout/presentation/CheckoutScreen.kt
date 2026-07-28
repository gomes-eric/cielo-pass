package com.cielo.cielopass.features.checkout.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cielo.cielopass.R
import com.cielo.cielopass.core.event.domain.model.Event
import com.cielo.cielopass.core.theme.CieloPassTheme
import com.cielo.cielopass.features.checkout.presentation.CheckoutEffect.NavigateBack
import com.cielo.cielopass.features.checkout.presentation.CheckoutEffect.NavigateToPaymentResult
import com.cielo.cielopass.features.checkout.presentation.CheckoutEffect.ShowToast
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.DocumentChanged
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.EmailChanged
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.LoadEvent
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.NameChanged
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.ProcessPayment
import com.cielo.cielopass.features.checkout.presentation.CheckoutEvent.QuantityChanged
import com.cielo.cielopass.features.checkout.presentation.components.BottomCheckoutBar
import com.cielo.cielopass.features.checkout.presentation.components.CustomerInfoCard
import com.cielo.cielopass.features.checkout.presentation.components.OrderSummaryCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutScreen(
    eventId: String,
    viewModel: CheckoutViewModel = koinViewModel(key = eventId),
    onNavigateBack: () -> Unit = {},
    onNavigateToResult: (status: String, transactionId: String?, errorMessage: String?) -> Unit = { _, _, _ -> },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        viewModel.onEvent(LoadEvent(eventId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is NavigateToPaymentResult -> {
                    onNavigateToResult(effect.status, effect.transactionId, effect.errorMessage)
                }

                is NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    CheckoutContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    state: CheckoutState,
    onEvent: (CheckoutEvent) -> Unit,
    onNavigateBack: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.checkout_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        bottomBar = {
            if (state.event != null) {
                BottomCheckoutBar(
                    totalPrice = state.totalPrice,
                    isFormValid = state.isFormValid,
                    isProcessing = state.isProcessingPayment,
                    onSubmitClick = { onEvent(ProcessPayment) },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                state.event != null -> {
                    val scrollState = rememberScrollState()
                    val keyboardHeight = WindowInsets.ime.asPaddingValues().calculateBottomPadding()

                    LaunchedEffect(keyboardHeight) {
                        if (keyboardHeight > 0.dp) {
                            val targetScroll = (scrollState.maxValue * 0.45f).toInt()
                            scrollState.animateScrollTo(targetScroll)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .imePadding()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        OrderSummaryCard(
                            event = state.event,
                            quantity = state.quantity,
                            totalPrice = state.totalPrice,
                            quantityError = state.quantityError,
                            onQuantityChange = { onEvent(QuantityChanged(it)) },
                        )

                        CustomerInfoCard(
                            name = state.name,
                            nameError = state.nameError,
                            email = state.email,
                            emailError = state.emailError,
                            document = state.document,
                            documentError = state.documentError,
                            onNameChange = { onEvent(NameChanged(it)) },
                            onEmailChange = { onEvent(EmailChanged(it)) },
                            onDocumentChange = { onEvent(DocumentChanged(it)) },
                        )

                        if (state.error != null) {
                            Text(
                                text = state.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Valid State")
@Composable
private fun CheckoutScreenSuccessPreview() {
    CieloPassTheme {
        CheckoutContent(
            state = CheckoutState(
                isLoading = false,
                name = "Carlos Silva",
                email = "carlos.silva@email.com",
                document = "123.456.789-00",
                event = Event(
                    id = "1",
                    title = "Festival Cielo Pass 2026",
                    description = "Descrição do evento",
                    date = "15 Ago 2026",
                    venue = "Allianz Parque",
                    price = 250.0,
                    totalTickets = 500,
                    availableTickets = 50,
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Invalid State")
@Composable
private fun CheckoutScreenInvalidPreview() {
    CieloPassTheme {
        CheckoutContent(
            state = CheckoutState(
                isLoading = false,
                name = "Carlos",
                nameError = "Informe o nome completo",
                email = "carlos@",
                emailError = "E-mail inválido",
                document = "123",
                documentError = "CPF inválido",
                event = Event(
                    id = "1",
                    title = "Festival Cielo Pass 2026",
                    description = "Descrição do evento",
                    date = "15 Ago 2026",
                    venue = "Allianz Parque",
                    price = 250.0,
                    totalTickets = 500,
                    availableTickets = 50,
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun CheckoutScreenLoadingPreview() {
    CieloPassTheme {
        CheckoutContent(
            state = CheckoutState(isLoading = true),
            onEvent = {},
        )
    }
}
