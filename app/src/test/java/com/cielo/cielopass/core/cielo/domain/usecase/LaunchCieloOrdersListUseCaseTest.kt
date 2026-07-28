package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink
import com.cielo.cielopass.core.cielo.domain.model.CieloOrdersListRequest
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LaunchCieloOrdersListUseCaseTest {
    private lateinit var cieloRepository: CieloDeeplinkRepository
    private lateinit var useCase: LaunchCieloOrdersListUseCase

    @Before
    fun setUp() {
        cieloRepository = mockk()
        useCase = LaunchCieloOrdersListUseCase(cieloRepository)
    }

    @Test
    fun `given orders list request when invoke then launch deeplink`() {
        // GIVEN
        val request = CieloOrdersListRequest(clientId = "client", accessToken = "token", pageSize = 5, page = 1)
        every { cieloRepository.launchDeeplink(CieloDeeplink.OrdersList(request)) } just runs

        // WHEN
        useCase(request)

        // THEN
        verify(exactly = 1) { cieloRepository.launchDeeplink(CieloDeeplink.OrdersList(request)) }
    }
}
