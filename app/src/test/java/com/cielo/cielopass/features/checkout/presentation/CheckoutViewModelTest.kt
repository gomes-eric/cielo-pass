package com.cielo.cielopass.features.checkout.presentation

import androidx.lifecycle.SavedStateHandle
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloPaymentUseCase
import com.cielo.cielopass.core.event.domain.model.Event
import com.cielo.cielopass.core.event.domain.repository.EventRepository
import com.cielo.cielopass.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CheckoutViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var eventRepository: EventRepository
    private lateinit var launchCieloPaymentUseCase: LaunchCieloPaymentUseCase
    private lateinit var viewModel: CheckoutViewModel

    @Before
    fun setUp() {
        eventRepository = mockk()
        launchCieloPaymentUseCase = mockk()
    }

    @Test
    fun `given event id in saved state handle when viewModel initialized then load event`() =
        runTest {
            // GIVEN
            val eventId = "event-123"
            val savedStateHandle = SavedStateHandle(mapOf("eventId" to eventId))
            val event = Event(
                id = eventId,
                title = "Fest",
                description = "Desc",
                date = "2026-10-10",
                venue = "Stage",
                price = 50.0,
                totalTickets = 10,
                availableTickets = 5,
            )
            coEvery { eventRepository.getById(eventId) } returns event

            // WHEN
            viewModel = CheckoutViewModel(eventRepository, launchCieloPaymentUseCase, savedStateHandle)

            // THEN
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals(event, state.event)
            assertEquals(1, state.quantity)
        }

    @Test
    fun `given invalid event id when LoadEvent dispatched then set error`() =
        runTest {
            // GIVEN
            coEvery { eventRepository.getById("invalid") } returns null
            viewModel = CheckoutViewModel(eventRepository, launchCieloPaymentUseCase)

            // WHEN
            viewModel.onEvent(CheckoutEvent.LoadEvent("invalid"))

            // THEN
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertNotNull(state.error)
        }

    @Test
    fun `given invalid name input when NameChanged dispatched then set nameError`() =
        runTest {
            // GIVEN
            viewModel = CheckoutViewModel(eventRepository, launchCieloPaymentUseCase)

            // WHEN
            viewModel.onEvent(CheckoutEvent.NameChanged("Invalid"))

            // THEN
            val state = viewModel.state.value
            assertNotNull(state.nameError)

            // WHEN (valid name)
            viewModel.onEvent(CheckoutEvent.NameChanged("Valid Name"))

            // THEN
            assertNull(viewModel.state.value.nameError)
        }

    @Test
    fun `given invalid email input when EmailChanged dispatched then set emailError`() =
        runTest {
            // GIVEN
            viewModel = CheckoutViewModel(eventRepository, launchCieloPaymentUseCase)

            // WHEN
            viewModel.onEvent(CheckoutEvent.EmailChanged("not-an-email"))

            // THEN
            assertNotNull(viewModel.state.value.emailError)

            // WHEN (valid email)
            viewModel.onEvent(CheckoutEvent.EmailChanged("user@test.com"))

            // THEN
            assertNull(viewModel.state.value.emailError)
        }

    @Test
    fun `given valid form when ProcessPayment dispatched then call launch payment use case`() =
        runTest {
            // GIVEN
            val eventId = "event-1"
            val event = Event(
                id = eventId,
                title = "Fest",
                description = "Desc",
                date = "2026-10-10",
                venue = "Stage",
                price = 50.0,
                totalTickets = 10,
                availableTickets = 5,
            )
            coEvery { eventRepository.getById(eventId) } returns event
            coEvery { launchCieloPaymentUseCase(any()) } returns LaunchPaymentResult.Success

            val savedStateHandle = SavedStateHandle(mapOf("eventId" to eventId))
            viewModel = CheckoutViewModel(eventRepository, launchCieloPaymentUseCase, savedStateHandle)

            // Fill form fields
            viewModel.onEvent(CheckoutEvent.NameChanged("John Doe"))
            viewModel.onEvent(CheckoutEvent.EmailChanged("john@example.com"))
            viewModel.onEvent(CheckoutEvent.DocumentChanged("123.456.789-00"))
            viewModel.onEvent(CheckoutEvent.QuantityChanged(2))

            assertTrue(viewModel.state.value.isFormValid)

            // WHEN
            viewModel.onEvent(CheckoutEvent.ProcessPayment)

            // THEN
            coVerify(exactly = 1) { launchCieloPaymentUseCase(any()) }
            val effect = viewModel.effect.first()
            assertTrue(effect is CheckoutEffect.ShowToast)
        }

    @Test
    fun `given error set when DismissError dispatched then clear error`() =
        runTest {
            // GIVEN
            coEvery { eventRepository.getById("missing") } returns null
            viewModel = CheckoutViewModel(eventRepository, launchCieloPaymentUseCase)
            viewModel.onEvent(CheckoutEvent.LoadEvent("missing"))
            assertNotNull(viewModel.state.value.error)

            // WHEN
            viewModel.onEvent(CheckoutEvent.DismissError)

            // THEN
            assertNull(viewModel.state.value.error)
        }
}
