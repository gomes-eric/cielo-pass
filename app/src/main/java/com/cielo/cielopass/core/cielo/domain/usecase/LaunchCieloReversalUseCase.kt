package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.Reversal
import com.cielo.cielopass.core.cielo.domain.model.CieloReversalRequest
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository

class LaunchCieloReversalUseCase(
    private val cieloRepository: CieloDeeplinkRepository,
) {
    operator fun invoke(request: CieloReversalRequest) {
        cieloRepository.launchDeeplink(Reversal(request))
    }
}
