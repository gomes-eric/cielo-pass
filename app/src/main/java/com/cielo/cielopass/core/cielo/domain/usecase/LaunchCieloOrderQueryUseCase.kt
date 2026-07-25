package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.OrderQuery
import com.cielo.cielopass.core.cielo.domain.model.CieloOrderQueryRequest
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository

class LaunchCieloOrderQueryUseCase(
    private val cieloRepository: CieloDeeplinkRepository,
) {
    operator fun invoke(request: CieloOrderQueryRequest) {
        cieloRepository.launchDeeplink(OrderQuery(request))
    }
}
