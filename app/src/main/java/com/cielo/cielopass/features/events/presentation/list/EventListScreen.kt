package com.cielo.cielopass.features.events.presentation.list

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.cielo.cielopass.features.events.presentation.list.EventListEffect.NavigateToDetails
import com.cielo.cielopass.features.events.presentation.list.EventListEffect.ShowToast
import com.cielo.cielopass.features.events.presentation.list.EventListEvent.AddEvent
import com.cielo.cielopass.features.events.presentation.list.EventListEvent.ClearEvents
import com.cielo.cielopass.features.events.presentation.list.EventListEvent.DismissAddDialog
import com.cielo.cielopass.features.events.presentation.list.EventListEvent.DismissError
import com.cielo.cielopass.features.events.presentation.list.EventListEvent.LoadEvents
import com.cielo.cielopass.features.events.presentation.list.EventListEvent.OpenAddDialog
import com.cielo.cielopass.features.events.presentation.list.EventListEvent.SeedMockEvents
import com.cielo.cielopass.features.events.presentation.list.EventListEvent.SelectEvent
import com.cielo.cielopass.features.events.presentation.list.EventListEvent.ToggleSpeedDial
import com.cielo.cielopass.features.events.presentation.list.components.AddEventDialog
import com.cielo.cielopass.features.events.presentation.list.components.EventListEmptyStateView
import com.cielo.cielopass.features.events.presentation.list.components.EventListErrorStateView
import com.cielo.cielopass.features.events.presentation.list.components.EventListFabMenu
import com.cielo.cielopass.features.events.presentation.list.components.EventSummaryCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun EventListScreen(
    viewModel: EventListViewModel = koinViewModel(),
    onNavigateToDetails: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NavigateToDetails -> {
                    onNavigateToDetails(effect.eventId)
                }

                is ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    EventListContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListContent(
    state: EventListState,
    onEvent: (EventListEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        floatingActionButton = {
            EventListFabMenu(
                isExpanded = state.isSpeedDialExpanded,
                onToggleMenu = { onEvent(ToggleSpeedDial) },
                onAddEvent = { onEvent(OpenAddDialog) },
                onSeedEvents = { onEvent(SeedMockEvents) },
                onClearEvents = { onEvent(ClearEvents) },
            )
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
                    EventListErrorStateView(
                        errorMessage = state.error,
                        onRetry = { onEvent(LoadEvents) },
                        onDismiss = { onEvent(DismissError) },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                state.events.isEmpty() -> {
                    EventListEmptyStateView(
                        onSeedEvents = { onEvent(SeedMockEvents) },
                        onAddEvent = { onEvent(OpenAddDialog) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = state.events,
                            key = { it.id },
                        ) { event ->
                            EventSummaryCard(
                                event = event,
                                onClick = { onEvent(SelectEvent(event.id)) },
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.isAddDialogOpen) {
        AddEventDialog(
            onDismiss = { onEvent(DismissAddDialog) },
            onConfirm = { title, desc, date, venue, price, total, avail, imgUrl ->
                onEvent(
                    AddEvent(
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

@Preview(showBackground = true, name = "Success State")
@Composable
private fun EventListScreenSuccessPreview() {
    CieloPassTheme {
        EventListContent(
            state = EventListState(
                isLoading = false,
                events = listOf(
                    Event(
                        id = "1",
                        title = "Festival Cielo Pass 2026",
                        description = "O maior festival do ano",
                        date = "15 Ago 2026 • 18:00",
                        venue = "Allianz Parque - SP",
                        price = 250.0,
                        totalTickets = 500,
                        availableTickets = 15,
                    ),
                    Event(
                        id = "2",
                        title = "Noite de Jazz",
                        description = "Música ao vivo de alta qualidade",
                        date = "20 Ago 2026 • 20:00",
                        venue = "Teatro Bradesco - SP",
                        price = 120.0,
                        totalTickets = 150,
                        availableTickets = 0,
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun EventListScreenLoadingPreview() {
    CieloPassTheme {
        EventListContent(
            state = EventListState(isLoading = true),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun EventListScreenEmptyPreview() {
    CieloPassTheme {
        EventListContent(
            state = EventListState(isLoading = false, events = emptyList()),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
private fun EventListScreenErrorPreview() {
    CieloPassTheme {
        EventListContent(
            state = EventListState(isLoading = false, error = "Falha ao conectar com o banco de dados local."),
            onEvent = {},
        )
    }
}
