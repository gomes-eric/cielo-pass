package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.Establishments
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository

class LaunchCieloEstablishmentsUseCase(
    private val cieloRepository: CieloDeeplinkRepository,
) {
    operator fun invoke() {
        cieloRepository.launchDeeplink(Establishments)
    }
}
