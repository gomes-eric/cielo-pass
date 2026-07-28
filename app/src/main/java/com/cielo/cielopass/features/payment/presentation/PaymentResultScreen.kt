package com.cielo.cielopass.features.payment.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cielo.cielopass.R
import com.cielo.cielopass.core.navigation.PaymentResultRoute
import com.cielo.cielopass.core.theme.CieloPassTheme
import com.cielo.cielopass.features.payment.presentation.PaymentResultEffect.NavigateToCheckout
import com.cielo.cielopass.features.payment.presentation.PaymentResultEffect.NavigateToHome
import com.cielo.cielopass.features.payment.presentation.PaymentResultEffect.ShowToast
import com.cielo.cielopass.features.payment.presentation.PaymentResultEvent.BackToHome
import com.cielo.cielopass.features.payment.presentation.PaymentResultEvent.CheckPendingStatus
import com.cielo.cielopass.features.payment.presentation.PaymentResultEvent.Init
import com.cielo.cielopass.features.payment.presentation.PaymentResultEvent.RetryPayment
import com.cielo.cielopass.features.payment.presentation.components.QrCodeCard
import com.cielo.cielopass.features.payment.presentation.components.ResultHeader
import com.cielo.cielopass.features.payment.presentation.components.TransactionDetailsCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaymentResultScreen(
    route: PaymentResultRoute,
    viewModel: PaymentResultViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToCheckout: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(route) {
        viewModel.onEvent(
            Init(
                status = route.status,
                transactionId = route.transactionId,
                errorMessage = route.errorMessage,
                amount = route.amount,
                reference = route.transactionId,
            ),
        )
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NavigateToHome -> {
                    onNavigateToHome()
                }

                is NavigateToCheckout -> {
                    onNavigateToCheckout()
                }

                is ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    PaymentResultContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentResultContent(
    state: PaymentResultState,
    onEvent: (PaymentResultEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.payment_result_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    when {
                        state.isApproved -> {
                            ResultHeader(
                                icon = Icons.Default.CheckCircle,
                                iconColor = Color(0xFF2E7D32),
                                title = stringResource(R.string.payment_approved_title),
                                subtitle = stringResource(R.string.payment_approved_subtitle),
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            TransactionDetailsCard(
                                reference = state.transactionId ?: stringResource(R.string.unknown),
                                amountCents = state.amount,
                                statusText = stringResource(R.string.payment_status_approved),
                                statusColor = Color(0xFF2E7D32),
                            )

                            if (!state.transactionId.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))

                                QrCodeCard(
                                    transactionId = state.transactionId,
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { onEvent(BackToHome) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = stringResource(R.string.action_back_to_events),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        state.isCancelled -> {
                            ResultHeader(
                                icon = Icons.Default.Cancel,
                                iconColor = Color(0xFFE65100),
                                title = stringResource(R.string.payment_cancelled_title),
                                subtitle = state.errorMessage ?: stringResource(R.string.payment_cancelled_subtitle),
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { onEvent(RetryPayment) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = stringResource(R.string.action_try_again),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { onEvent(BackToHome) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.action_back_to_events),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }

                        state.isFailed -> {
                            ResultHeader(
                                icon = Icons.Default.Error,
                                iconColor = MaterialTheme.colorScheme.error,
                                title = stringResource(R.string.payment_failed_title),
                                subtitle = state.errorMessage ?: stringResource(R.string.payment_failed_subtitle),
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { onEvent(RetryPayment) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = stringResource(R.string.action_try_again),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { onEvent(BackToHome) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.action_back_to_events),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }

                        else -> {
                            ResultHeader(
                                icon = Icons.AutoMirrored.Filled.Help,
                                iconColor = Color(0xFF6A1B9A),
                                title = stringResource(R.string.payment_unknown_title),
                                subtitle = state.errorMessage ?: stringResource(R.string.payment_unknown_subtitle),
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { onEvent(CheckPendingStatus) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = stringResource(R.string.action_check_pending_status),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { onEvent(BackToHome) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.action_back_to_events),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Success Outcome")
@Composable
private fun PaymentResultSuccessPreview() {
    CieloPassTheme {
        PaymentResultContent(
            state = PaymentResultState(
                isLoading = false,
                status = "APPROVED",
                transactionId = "ba583f85-9252-48b5-8fed-12719ff058b9",
                amount = 25000L,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Cancelled Outcome")
@Composable
private fun PaymentResultCancelledPreview() {
    CieloPassTheme {
        PaymentResultContent(
            state = PaymentResultState(
                isLoading = false,
                status = "CANCELLED",
                errorMessage = "Operação cancelada pelo usuário na LIO",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Error Outcome")
@Composable
private fun PaymentResultErrorPreview() {
    CieloPassTheme {
        PaymentResultContent(
            state = PaymentResultState(
                isLoading = false,
                status = "FAILED",
                errorMessage = "Saldo insuficiente ou cartão recusado",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Unknown Outcome")
@Composable
private fun PaymentResultUnknownPreview() {
    CieloPassTheme {
        PaymentResultContent(
            state = PaymentResultState(
                isLoading = false,
                status = "UNKNOWN",
                errorMessage = "Aguardando confirmação do terminal",
            ),
            onEvent = {},
        )
    }
}
