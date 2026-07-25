package com.cielo.cielopass.features.splash.domain.usecase

import com.cielo.cielopass.core.constants.SplashConstants.MSG_CHECKING_CREDENTIALS
import com.cielo.cielopass.core.credentials.domain.repository.CieloCredentialsRepository
import com.cielo.cielopass.features.splash.domain.model.SplashConfig
import com.cielo.cielopass.features.splash.domain.model.SplashStep
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InitializeAppUseCaseTest {
    private val credentialsRepository: CieloCredentialsRepository = mockk(relaxed = true)
    private lateinit var useCase: InitializeAppUseCase

    @Before
    fun setUp() {
        useCase = InitializeAppUseCase(credentialsRepository)
    }

    @Test
    fun `invoke should emit progress steps and completed step`() =
        runBlocking {
            // Given
            val expectedConfig = SplashConfig(isInitialized = true)

            // When
            val steps = useCase().toList()

            // Then
            assertTrue(steps.any { it is SplashStep.Progress && it.message == MSG_CHECKING_CREDENTIALS })
            val lastStep = steps.last()
            assertTrue(lastStep is SplashStep.Completed)
            assertEquals(expectedConfig, (lastStep as SplashStep.Completed).config)
        }
}
