package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.Print
import com.cielo.cielopass.core.cielo.domain.model.CieloPrintRequest
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository

class LaunchCieloPrintUseCase(
    private val cieloRepository: CieloDeeplinkRepository,
) {
    operator fun invoke(request: CieloPrintRequest) {
        cieloRepository.launchDeeplink(Print(request))
    }
}
