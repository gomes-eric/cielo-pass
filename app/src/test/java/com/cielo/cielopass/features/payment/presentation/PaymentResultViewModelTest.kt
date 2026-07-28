package com.cielo.cielopass.features.payment.presentation

import com.cielo.cielopass.core.event.domain.model.Event
import com.cielo.cielopass.core.event.domain.repository.EventRepository
import com.cielo.cielopass.core.transaction.domain.model.Transaction
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository
import com.cielo.cielopass.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PaymentResultViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var transactionRepository: TransactionRepository
    private lateinit var eventRepository: EventRepository
    private lateinit var viewModel: PaymentResultViewModel

    @Before
    fun setUp() {
        transactionRepository = mockk()
        eventRepository = mockk()
        viewModel = PaymentResultViewModel(
            transactionRepository = transactionRepository,
            eventRepository = eventRepository,
        )
    }

    @Test
    fun `given approved status when Init dispatched then update state and deduct inventory if not deducted`() =
        runTest {
            // GIVEN
            val txId = "tx-123"
            val eventId = "event-456"
            val transaction = Transaction(
                id = txId,
                amount = 10000L,
                status = "APPROVED",
                eventId = eventId,
                quantity = 2,
                inventoryDeducted = false,
            )
            val event = Event(
                id = eventId,
                title = "Festival",
                description = "Desc",
                date = "2026-10-10",
                venue = "Arena",
                price = 50.0,
                totalTickets = 100,
                availableTickets = 20,
            )

            coEvery { transactionRepository.getById(txId) } returns transaction
            coEvery { eventRepository.getById(eventId) } returns event

            val updatedEventSlot = slot<Event>()
            coEvery { eventRepository.update(capture(updatedEventSlot)) } just runs

            val updatedTxSlot = slot<Transaction>()
            coEvery { transactionRepository.update(capture(updatedTxSlot)) } just runs

            // WHEN
            viewModel.onEvent(
                PaymentResultEvent.Init(
                    status = "APPROVED",
                    transactionId = txId,
                    errorMessage = null,
                    amount = 10000L,
                    reference = txId,
                ),
            )

            // THEN
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals("APPROVED", state.status)
            assertEquals(txId, state.transactionId)
            assertEquals(10000L, state.amount)

            // Verify inventory deduction (20 - 2 = 18)
            assertEquals(18, updatedEventSlot.captured.availableTickets)
            assertTrue(updatedTxSlot.captured.inventoryDeducted)

            coVerify(exactly = 1) { eventRepository.update(any()) }
            coVerify(exactly = 1) { transactionRepository.update(any()) }
        }

    @Test
    fun `given BackToHome dispatched then emit NavigateToHome effect`() =
        runTest {
            // WHEN
            viewModel.onEvent(PaymentResultEvent.BackToHome)

            // THEN
            val effect = viewModel.effect.first()
            assertTrue(effect is PaymentResultEffect.NavigateToHome)
        }

    @Test
    fun `given RetryPayment dispatched then emit NavigateToCheckout effect`() =
        runTest {
            // WHEN
            viewModel.onEvent(PaymentResultEvent.RetryPayment)

            // THEN
            val effect = viewModel.effect.first()
            assertTrue(effect is PaymentResultEffect.NavigateToCheckout)
        }

    @Test
    fun `given active pending transaction when CheckPendingStatus dispatched then update state with pending info`() =
        runTest {
            // GIVEN
            val pending = Transaction(
                id = "pending-123",
                amount = 5000L,
                status = "PENDING",
                errorMessage = null,
            )
            coEvery { transactionRepository.getPending() } returns pending

            // WHEN
            viewModel.onEvent(PaymentResultEvent.CheckPendingStatus)

            // THEN
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals("PENDING", state.status)
            assertEquals("pending-123", state.transactionId)
            assertEquals(5000L, state.amount)
        }
}
