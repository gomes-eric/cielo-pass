package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.model.CieloOrderQueryRequest
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LaunchCieloOrderQueryUseCaseTest {
    private lateinit var cieloRepository: CieloDeeplinkRepository
    private lateinit var useCase: LaunchCieloOrderQueryUseCase

    @Before
    fun setUp() {
        cieloRepository = mockk()
        useCase = LaunchCieloOrderQueryUseCase(cieloRepository)
    }

    @Test
    fun `given order query request when invoke then launch deeplink`() {
        // GIVEN
        val request = CieloOrderQueryRequest(orderId = "ord-123", amount = 1000L)
        every { cieloRepository.launchDeeplink(CieloDeeplink.OrderQuery(request)) } just runs

        // WHEN
        useCase(request)

        // THEN
        verify(exactly = 1) { cieloRepository.launchDeeplink(CieloDeeplink.OrderQuery(request)) }
    }
}
