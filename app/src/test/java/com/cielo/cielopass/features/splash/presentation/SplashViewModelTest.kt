package com.cielo.cielopass.features.splash.presentation

import com.cielo.cielopass.features.splash.domain.model.SplashConfig
import com.cielo.cielopass.features.splash.domain.model.SplashStep
import com.cielo.cielopass.features.splash.domain.usecase.InitializeAppUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val initializeAppUseCase: InitializeAppUseCase = mockk()
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init collects steps and updates state and emits NavigateToEvents effect`() =
        runTest {
            // Given
            val config = SplashConfig(isInitialized = true)
            val steps = flowOf(
                SplashStep.Progress("Verificando credenciais..."),
                SplashStep.Completed(message = "Sistema pronto!", config = config),
            )
            every { initializeAppUseCase() } returns steps

            // When
            viewModel = SplashViewModel(initializeAppUseCase)
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals("Sistema pronto!", state.statusText)
            assertEquals(config, state.config)
            assertNull(state.error)
        }

    @Test
    fun `init handles failure and sets error state`() =
        runTest {
            // Given
            val exception = RuntimeException("Initialization failed")
            every { initializeAppUseCase() } returns flow { throw exception }

            // When
            viewModel = SplashViewModel(initializeAppUseCase)
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals("Initialization failed", state.error)
        }
}
