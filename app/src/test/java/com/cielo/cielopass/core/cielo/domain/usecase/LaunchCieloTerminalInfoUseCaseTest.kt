package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LaunchCieloTerminalInfoUseCaseTest {
    private lateinit var cieloRepository: CieloDeeplinkRepository
    private lateinit var useCase: LaunchCieloTerminalInfoUseCase

    @Before
    fun setUp() {
        cieloRepository = mockk()
        useCase = LaunchCieloTerminalInfoUseCase(cieloRepository)
    }

    @Test
    fun `given terminal info request when invoke then launch deeplink`() {
        // GIVEN
        every { cieloRepository.launchDeeplink(CieloDeeplink.TerminalInfo) } just runs

        // WHEN
        useCase()

        // THEN
        verify(exactly = 1) { cieloRepository.launchDeeplink(CieloDeeplink.TerminalInfo) }
    }
}
