package com.cielo.cielopass.features.events.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloPaymentUseCase
import com.cielo.cielopass.core.event.domain.model.Event
import com.cielo.cielopass.core.event.domain.repository.EventRepository
import com.cielo.cielopass.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventDetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var eventRepository: EventRepository
    private lateinit var launchCieloPaymentUseCase: LaunchCieloPaymentUseCase
    private lateinit var viewModel: EventDetailsViewModel

    @Before
    fun setUp() {
        eventRepository = mockk()
        launchCieloPaymentUseCase = mockk()
    }

    @Test
    fun `given event id when viewModel initialized then observe event details`() =
        runTest {
            // GIVEN
            val eventId = "event-1"
            val event = Event(
                id = eventId,
                title = "Concert",
                description = "Live Concert",
                date = "2026-11-11",
                venue = "Arena",
                price = 120.0,
                totalTickets = 100,
                availableTickets = 50,
            )
            val savedStateHandle = SavedStateHandle(mapOf("eventId" to eventId))
            coEvery { eventRepository.observeById(eventId) } returns flowOf(event)

            // WHEN
            viewModel = EventDetailsViewModel(eventRepository, launchCieloPaymentUseCase, savedStateHandle)

            // THEN
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals(event, state.event)
            assertNull(state.error)
        }

    @Test
    fun `given available tickets when BuyTicket dispatched then launch payment use case`() =
        runTest {
            // GIVEN
            val eventId = "event-1"
            val event = Event(
                id = eventId,
                title = "Concert",
                description = "Live Concert",
                date = "2026-11-11",
                venue = "Arena",
                price = 100.0,
                totalTickets = 100,
                availableTickets = 10,
            )
            val savedStateHandle = SavedStateHandle(mapOf("eventId" to eventId))
            coEvery { eventRepository.observeById(eventId) } returns flowOf(event)
            coEvery { launchCieloPaymentUseCase(any()) } returns LaunchPaymentResult.Success

            viewModel = EventDetailsViewModel(eventRepository, launchCieloPaymentUseCase, savedStateHandle)

            // WHEN
            viewModel.onEvent(EventDetailsEvent.BuyTicket)

            // THEN
            coVerify(exactly = 1) { launchCieloPaymentUseCase(any()) }
            val effect = viewModel.effect.first()
            assertTrue(effect is EventDetailsEffect.ShowToast)
        }

    @Test
    fun `given delete confirmation open when ConfirmDelete dispatched then delete event and navigate back`() =
        runTest {
            // GIVEN
            val eventId = "event-1"
            val event = Event(
                id = eventId,
                title = "Concert",
                description = "Live Concert",
                date = "2026-11-11",
                venue = "Arena",
                price = 100.0,
                totalTickets = 100,
                availableTickets = 10,
            )
            val savedStateHandle = SavedStateHandle(mapOf("eventId" to eventId))
            coEvery { eventRepository.observeById(eventId) } returns flowOf(event)
            coEvery { eventRepository.deleteById(eventId) } just runs

            viewModel = EventDetailsViewModel(eventRepository, launchCieloPaymentUseCase, savedStateHandle)
            viewModel.onEvent(EventDetailsEvent.OpenDeleteConfirm)
            assertTrue(viewModel.state.value.isDeleteConfirmDialogOpen)

            // WHEN
            viewModel.onEvent(EventDetailsEvent.ConfirmDelete)

            // THEN
            coVerify(exactly = 1) { eventRepository.deleteById(eventId) }
            assertFalse(viewModel.state.value.isDeleteConfirmDialogOpen)
        }

    @Test
    fun `given edit dialog open when UpdateEvent dispatched then update event`() =
        runTest {
            // GIVEN
            val eventId = "event-1"
            val event = Event(
                id = eventId,
                title = "Old Title",
                description = "Old Desc",
                date = "2026-11-11",
                venue = "Arena",
                price = 100.0,
                totalTickets = 100,
                availableTickets = 10,
            )
            val savedStateHandle = SavedStateHandle(mapOf("eventId" to eventId))
            coEvery { eventRepository.observeById(eventId) } returns flowOf(event)
            coEvery { eventRepository.update(any()) } just runs

            viewModel = EventDetailsViewModel(eventRepository, launchCieloPaymentUseCase, savedStateHandle)
            viewModel.onEvent(EventDetailsEvent.OpenEditDialog)
            assertTrue(viewModel.state.value.isEditDialogOpen)

            // WHEN
            viewModel.onEvent(
                EventDetailsEvent.UpdateEvent(
                    title = "New Title",
                    description = "New Desc",
                    date = "2026-12-12",
                    venue = "New Venue",
                    price = 150.0,
                    totalTickets = 200,
                    availableTickets = 50,
                    imageUrl = null,
                ),
            )

            // THEN
            coVerify(exactly = 1) { eventRepository.update(any()) }
            assertFalse(viewModel.state.value.isEditDialogOpen)
        }
}
