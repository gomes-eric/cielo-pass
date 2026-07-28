package com.cielo.cielopass.features.checkout.presentation

import com.cielo.cielopass.core.event.domain.model.Event
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckoutStateTest {
    @Test
    fun `given valid name when validating name then returns true`() {
        // GIVEN
        val validName = "John Doe"

        // WHEN
        val isValid = CheckoutState.NAME_REGEX.matches(validName)

        // THEN
        assertTrue(isValid)
    }

    @Test
    fun `given single word or invalid name when validating name then returns false`() {
        // GIVEN
        val invalidName = "John"

        // WHEN
        val isValid = CheckoutState.NAME_REGEX.matches(invalidName)

        // THEN
        assertFalse(isValid)
    }

    @Test
    fun `given valid email when validating email then returns true`() {
        // GIVEN
        val validEmail = "user@example.com"

        // WHEN
        val isValid = CheckoutState.EMAIL_REGEX.matches(validEmail)

        // THEN
        assertTrue(isValid)
    }

    @Test
    fun `given invalid email format when validating email then returns false`() {
        // GIVEN
        val invalidEmail = "user@invalid"

        // WHEN
        val isValid = CheckoutState.EMAIL_REGEX.matches(invalidEmail)

        // THEN
        assertFalse(isValid)
    }

    @Test
    fun `given valid CPF or CNPJ document when validating document then returns true`() {
        // GIVEN
        val cpf = "123.456.789-01"
        val cnpj = "12.345.678/0001-90"

        // WHEN
        val isCpfValid = CheckoutState.validateDocument(cpf)
        val isCnpjValid = CheckoutState.validateDocument(cnpj)

        // THEN
        assertTrue(isCpfValid)
        assertTrue(isCnpjValid)
    }

    @Test
    fun `given invalid document string when validating document then returns false`() {
        // GIVEN
        val invalidDoc = "12345"

        // WHEN
        val isValid = CheckoutState.validateDocument(invalidDoc)

        // THEN
        assertFalse(isValid)
    }

    @Test
    fun `given event price and quantity when computing total price then return correct product`() {
        // GIVEN
        val event = Event(
            id = "e1",
            title = "Concert",
            description = "Desc",
            date = "2026-10-10",
            venue = "Arena",
            price = 150.0,
            totalTickets = 100,
            availableTickets = 50,
        )
        val state = CheckoutState(event = event, quantity = 3)

        // WHEN
        val total = state.totalPrice

        // THEN
        assertEquals(450.0, total, 0.001)
    }

    @Test
    fun `given valid form fields when checking form validity then returns true`() {
        // GIVEN
        val event = Event(
            id = "e1",
            title = "Concert",
            description = "Desc",
            date = "2026-10-10",
            venue = "Arena",
            price = 100.0,
            totalTickets = 10,
            availableTickets = 5,
        )
        val state = CheckoutState(
            isLoading = false,
            isProcessingPayment = false,
            event = event,
            name = "Jane Doe",
            email = "jane@example.com",
            document = "123.456.789-00",
            quantity = 2,
        )

        // WHEN
        val isValid = state.isFormValid

        // THEN
        assertTrue(isValid)
    }
}
