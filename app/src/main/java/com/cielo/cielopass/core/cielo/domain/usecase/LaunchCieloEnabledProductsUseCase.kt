package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.EnabledProducts
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository

class LaunchCieloEnabledProductsUseCase(
    private val cieloRepository: CieloDeeplinkRepository,
) {
    operator fun invoke() {
        cieloRepository.launchDeeplink(EnabledProducts)
    }
}
