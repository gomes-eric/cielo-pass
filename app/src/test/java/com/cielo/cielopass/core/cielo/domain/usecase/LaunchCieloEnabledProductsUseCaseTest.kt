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

class LaunchCieloEnabledProductsUseCaseTest {
    private lateinit var cieloRepository: CieloDeeplinkRepository
    private lateinit var useCase: LaunchCieloEnabledProductsUseCase

    @Before
    fun setUp() {
        cieloRepository = mockk()
        useCase = LaunchCieloEnabledProductsUseCase(cieloRepository)
    }

    @Test
    fun `given enabled products request when invoke then launch deeplink`() {
        // GIVEN
        every { cieloRepository.launchDeeplink(CieloDeeplink.EnabledProducts) } just runs

        // WHEN
        useCase()

        // THEN
        verify(exactly = 1) { cieloRepository.launchDeeplink(CieloDeeplink.EnabledProducts) }
    }
}
