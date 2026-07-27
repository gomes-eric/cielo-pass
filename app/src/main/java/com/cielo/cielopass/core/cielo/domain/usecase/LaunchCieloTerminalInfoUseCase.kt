package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.TerminalInfo
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository

class LaunchCieloTerminalInfoUseCase(
    private val cieloRepository: CieloDeeplinkRepository,
) {
    operator fun invoke() {
        cieloRepository.launchDeeplink(TerminalInfo)
    }
}
