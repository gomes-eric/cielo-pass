package com.cielo.cielopass.features.events.presentation.list

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

class EventListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var eventRepository: EventRepository
    private lateinit var viewModel: EventListViewModel

    @Before
    fun setUp() {
        eventRepository = mockk()
    }

    @Test
    fun `given events in repository when viewModel initialized then emit loaded events`() =
        runTest {
            // GIVEN
            val mockEvents = listOf(
                Event(
                    id = "1",
                    title = "Event 1",
                    description = "Desc 1",
                    date = "2026-10-10",
                    venue = "Venue 1",
                    price = 50.0,
                    totalTickets = 10,
                    availableTickets = 5,
                ),
            )
            coEvery { eventRepository.observeAll() } returns flowOf(mockEvents)

            // WHEN
            viewModel = EventListViewModel(eventRepository)

            // THEN
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals(mockEvents, state.events)
            assertNull(state.error)
        }

    @Test
    fun `given speed dial closed when ToggleSpeedDial dispatched then expand speed dial`() =
        runTest {
            // GIVEN
            coEvery { eventRepository.observeAll() } returns flowOf(emptyList())
            viewModel = EventListViewModel(eventRepository)
            assertFalse(viewModel.state.value.isSpeedDialExpanded)

            // WHEN
            viewModel.onEvent(EventListEvent.ToggleSpeedDial)

            // THEN
            assertTrue(viewModel.state.value.isSpeedDialExpanded)
        }

    @Test
    fun `given add dialog open when AddEvent dispatched then insert event and close dialog`() =
        runTest {
            // GIVEN
            coEvery { eventRepository.observeAll() } returns flowOf(emptyList())
            coEvery { eventRepository.insert(any<Event>()) } just runs
            viewModel = EventListViewModel(eventRepository)

            viewModel.onEvent(EventListEvent.OpenAddDialog)
            assertTrue(viewModel.state.value.isAddDialogOpen)

            // WHEN
            viewModel.onEvent(
                EventListEvent.AddEvent(
                    title = "New Show",
                    description = "Show Desc",
                    date = "2026-12-01",
                    venue = "Theater",
                    price = 80.0,
                    totalTickets = 100,
                    availableTickets = 100,
                    imageUrl = null,
                ),
            )

            // THEN
            coVerify(exactly = 1) { eventRepository.insert(any<Event>()) }
            assertFalse(viewModel.state.value.isAddDialogOpen)
            val effect = viewModel.effect.first()
            assertTrue(effect is EventListEffect.ShowToast)
        }

    @Test
    fun `given SeedMockEvents dispatched then seed mock events into repository`() =
        runTest {
            // GIVEN
            coEvery { eventRepository.observeAll() } returns flowOf(emptyList())
            coEvery { eventRepository.insert(any<List<Event>>()) } just runs
            viewModel = EventListViewModel(eventRepository)

            // WHEN
            viewModel.onEvent(EventListEvent.SeedMockEvents)

            // THEN
            coVerify(exactly = 1) { eventRepository.insert(any<List<Event>>()) }
            val effect = viewModel.effect.first()
            assertTrue(effect is EventListEffect.ShowToast)
        }

    @Test
    fun `given ClearEvents dispatched then delete all events`() =
        runTest {
            // GIVEN
            coEvery { eventRepository.observeAll() } returns flowOf(emptyList())
            coEvery { eventRepository.deleteAll() } just runs
            viewModel = EventListViewModel(eventRepository)

            // WHEN
            viewModel.onEvent(EventListEvent.ClearEvents)

            // THEN
            coVerify(exactly = 1) { eventRepository.deleteAll() }
            val effect = viewModel.effect.first()
            assertTrue(effect is EventListEffect.ShowToast)
        }

    @Test
    fun `given event selected when SelectEvent dispatched then emit NavigateToDetails effect`() =
        runTest {
            // GIVEN
            coEvery { eventRepository.observeAll() } returns flowOf(emptyList())
            viewModel = EventListViewModel(eventRepository)

            // WHEN
            viewModel.onEvent(EventListEvent.SelectEvent("event-777"))

            // THEN
            val effect = viewModel.effect.first()
            assertTrue(effect is EventListEffect.NavigateToDetails)
            assertEquals("event-777", (effect as EventListEffect.NavigateToDetails).eventId)
        }
}
