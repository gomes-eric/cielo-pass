package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.model.CieloPrintRequest
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LaunchCieloPrintUseCaseTest {
    private lateinit var cieloRepository: CieloDeeplinkRepository
    private lateinit var useCase: LaunchCieloPrintUseCase

    @Before
    fun setUp() {
        cieloRepository = mockk()
        useCase = LaunchCieloPrintUseCase(cieloRepository)
    }

    @Test
    fun `given print text request when invoke then launch deeplink`() {
        // GIVEN
        val request = CieloPrintRequest.Text("Sample Receipt Text")
        every { cieloRepository.launchDeeplink(CieloDeeplink.Print(request)) } just runs

        // WHEN
        useCase(request)

        // THEN
        verify(exactly = 1) { cieloRepository.launchDeeplink(CieloDeeplink.Print(request)) }
    }
}
