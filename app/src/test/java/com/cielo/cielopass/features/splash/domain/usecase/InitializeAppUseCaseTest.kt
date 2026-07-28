package com.cielo.cielopass.features.splash.domain.usecase

import com.cielo.cielopass.core.credentials.domain.model.CieloCredentials
import com.cielo.cielopass.core.credentials.domain.repository.CieloCredentialsRepository
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository
import com.cielo.cielopass.features.splash.domain.model.SplashStep
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InitializeAppUseCaseTest {
    private lateinit var credentialsRepository: CieloCredentialsRepository
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var useCase: InitializeAppUseCase

    @Before
    fun setUp() {
        credentialsRepository = mockk()
        transactionRepository = mockk()
        useCase = InitializeAppUseCase(
            credentialsRepository = credentialsRepository,
            transactionRepository = transactionRepository,
        )
    }

    @Test
    fun `given app initialization when invoked then emit progress and completed splash steps`() =
        runTest {
            // GIVEN
            coEvery { credentialsRepository.saveCredentials(any(), any()) } returns Unit
            coEvery { credentialsRepository.credentials } returns flowOf(CieloCredentials("client-id", "token"))

            // WHEN
            val steps = useCase().toList()

            // THEN
            assertTrue(steps.size >= 3)
            assertTrue(steps[0] is SplashStep.Progress)
            assertTrue(steps[1] is SplashStep.Progress)
            assertTrue(steps[2] is SplashStep.Progress)
            assertTrue(steps.last() is SplashStep.Completed)

            val completedStep = steps.last() as SplashStep.Completed
            assertEquals("Sistema pronto!", completedStep.message)
        }
}
