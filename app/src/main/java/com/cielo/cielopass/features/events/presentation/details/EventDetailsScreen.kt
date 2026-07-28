package com.cielo.cielopass.features.events.presentation.details

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cielo.cielopass.R
import com.cielo.cielopass.core.event.domain.model.Event
import com.cielo.cielopass.core.theme.CieloPassTheme
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEffect.NavigateBack
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEffect.ShowToast
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.ConfirmDelete
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.DismissDeleteConfirm
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.DismissEditDialog
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.LoadEvent
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.OpenDeleteConfirm
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.OpenEditDialog
import com.cielo.cielopass.features.events.presentation.details.EventDetailsEvent.UpdateEvent
import com.cielo.cielopass.features.events.presentation.details.components.BottomPaymentCtaBar
import com.cielo.cielopass.features.events.presentation.details.components.DeleteConfirmDialog
import com.cielo.cielopass.features.events.presentation.details.components.EditEventDialog
import com.cielo.cielopass.features.events.presentation.details.components.ErrorDetailsView
import com.cielo.cielopass.features.events.presentation.details.components.HeaderImageBanner
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun EventDetailsScreen(
    eventId: String,
    viewModel: EventDetailsViewModel = koinViewModel(key = eventId),
    onNavigateBack: () -> Unit = {},
    onNavigateToCheckout: (eventId: String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        viewModel.onEvent(LoadEvent(eventId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NavigateBack -> {
                    onNavigateBack()
                }

                is ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    EventDetailsContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToCheckout = { onNavigateToCheckout(eventId) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsContent(
    state: EventDetailsUiState,
    onEvent: (EventDetailsEvent) -> Unit,
    onNavigateBack: () -> Unit = {},
    onNavigateToCheckout: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.event?.title ?: stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                actions = {
                    if (state.event != null) {
                        IconButton(onClick = { onEvent(OpenEditDialog) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.event_action_edit),
                            )
                        }
                        IconButton(onClick = { onEvent(OpenDeleteConfirm) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.event_action_delete),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        bottomBar = {
            if (state.event != null) {
                BottomPaymentCtaBar(
                    price = state.event.price,
                    isAvailable = state.event.availableTickets > 0,
                    onBuyClick = onNavigateToCheckout,
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

                state.error != null -> {
                    ErrorDetailsView(
                        message = state.error,
                        onBack = onNavigateBack,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                state.event != null -> {
                    EventDetailsBody(
                        event = state.event,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    if (state.isDeleteConfirmDialogOpen) {
        DeleteConfirmDialog(
            onDismiss = { onEvent(DismissDeleteConfirm) },
            onConfirm = { onEvent(ConfirmDelete) },
        )
    }

    if (state.isEditDialogOpen && state.event != null) {
        EditEventDialog(
            event = state.event,
            onDismiss = { onEvent(DismissEditDialog) },
            onConfirm = { title, desc, date, venue, price, total, avail, imgUrl ->
                onEvent(
                    UpdateEvent(
                        title = title,
                        description = desc,
                        date = date,
                        venue = venue,
                        price = price,
                        totalTickets = total,
                        availableTickets = avail,
                        imageUrl = imgUrl,
                    ),
                )
            },
        )
    }
}

@Composable
private fun EventDetailsBody(
    event: Event,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HeaderImageBanner(
            title = event.title,
            imageUrl = event.imageUrl,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(R.string.event_details_date_time_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Text(
                            text = event.date,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(R.string.event_details_venue_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Text(
                            text = event.venue,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )

                        Text(
                            text = stringResource(R.string.event_details_stock_label),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = formatCurrency(event.price),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                val progress = if (event.totalTickets > 0) {
                    (event.availableTickets.toFloat() / event.totalTickets.toFloat()).coerceIn(0f, 1f)
                } else {
                    0f
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(
                                R.string.event_details_stock_available,
                                event.availableTickets,
                                event.totalTickets,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )

                        Text(
                            text = stringResource(
                                R.string.event_details_stock_percentage,
                                (progress * 100).toInt(),
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (event.availableTickets > 10) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.event_details_about_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2f,
                )
            }
        }
    }
}

private fun formatCurrency(amount: Double): String =
    try {
        val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
        format.format(amount)
    } catch (_: Exception) {
        "R$ %.2f".format(amount)
    }

@Preview(showBackground = true, name = "Success State")
@Composable
private fun EventDetailsScreenSuccessPreview() {
    CieloPassTheme {
        EventDetailsContent(
            state = EventDetailsUiState(
                isLoading = false,
                event = Event(
                    id = "1",
                    title = "Festival Cielo Pass 2026",
                    description = "O maior festival de música e tecnologia do ano com atrações ao vivo no Allianz Parque.",
                    date = "15 Ago 2026 • 18:00",
                    venue = "Allianz Parque - São Paulo, SP",
                    price = 250.0,
                    totalTickets = 500,
                    availableTickets = 42,
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun EventDetailsScreenLoadingPreview() {
    CieloPassTheme {
        EventDetailsContent(
            state = EventDetailsUiState(isLoading = true),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Error / Empty State")
@Composable
private fun EventDetailsScreenErrorPreview() {
    CieloPassTheme {
        EventDetailsContent(
            state = EventDetailsUiState(isLoading = false, error = "Evento não encontrado."),
            onEvent = {},
        )
    }
}
