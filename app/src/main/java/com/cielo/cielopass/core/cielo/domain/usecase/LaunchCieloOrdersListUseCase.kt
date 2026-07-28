package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.OrdersList
import com.cielo.cielopass.core.cielo.domain.model.CieloOrdersListRequest
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository

class LaunchCieloOrdersListUseCase(
    private val cieloRepository: CieloDeeplinkRepository,
) {
    operator fun invoke(request: CieloOrdersListRequest) {
        cieloRepository.launchDeeplink(OrdersList(request))
    }
}
