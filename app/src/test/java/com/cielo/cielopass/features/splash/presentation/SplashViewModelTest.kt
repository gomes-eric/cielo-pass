package com.cielo.cielopass.features.splash.presentation

import com.cielo.cielopass.features.splash.domain.model.SplashConfig
import com.cielo.cielopass.features.splash.domain.model.SplashStep
import com.cielo.cielopass.features.splash.domain.usecase.InitializeAppUseCase
import com.cielo.cielopass.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SplashViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var initializeAppUseCase: InitializeAppUseCase
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setUp() {
        initializeAppUseCase = mockk()
    }

    @Test
    fun `given successful initialization when Init triggered then update state and emit NavigateToEvents effect`() =
        runTest {
            // GIVEN
            val steps = flowOf(
                SplashStep.Progress("Step 1"),
                SplashStep.Completed("Done", SplashConfig()),
            )
            coEvery { initializeAppUseCase() } returns steps

            // WHEN
            viewModel = SplashViewModel(initializeAppUseCase)

            // THEN
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            val effect = viewModel.effect.first()
            assertTrue(effect is SplashEffect.NavigateToEvents)
        }
}
