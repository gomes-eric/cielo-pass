package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.model.CieloReversalRequest
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LaunchCieloReversalUseCaseTest {
    private lateinit var cieloRepository: CieloDeeplinkRepository
    private lateinit var useCase: LaunchCieloReversalUseCase

    @Before
    fun setUp() {
        cieloRepository = mockk()
        useCase = LaunchCieloReversalUseCase(cieloRepository)
    }

    @Test
    fun `given reversal request when invoke then launch deeplink`() {
        // GIVEN
        val request = CieloReversalRequest(
            clientId = "client",
            accessToken = "token",
            orderId = "ord-123",
            value = 5000L,
            cieloCode = "c123",
            authCode = "a123",
        )
        every { cieloRepository.launchDeeplink(CieloDeeplink.Reversal(request)) } just runs

        // WHEN
        useCase(request)

        // THEN
        verify(exactly = 1) { cieloRepository.launchDeeplink(CieloDeeplink.Reversal(request)) }
    }
}
