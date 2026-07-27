package com.cielo.cielopass.features.splash.presentation

import com.cielo.cielopass.core.constants.SplashConstants.MSG_INITIALIZING_SYSTEM
import com.cielo.cielopass.features.splash.domain.model.SplashConfig

data class SplashState(
    val isLoading: Boolean = true,
    val statusText: String = MSG_INITIALIZING_SYSTEM,
    val config: SplashConfig? = null,
    val error: String? = null,
)

sealed interface SplashEvent {
    data object Init : SplashEvent
}

sealed interface SplashEffect {
    data object NavigateToEvents : SplashEffect
}
