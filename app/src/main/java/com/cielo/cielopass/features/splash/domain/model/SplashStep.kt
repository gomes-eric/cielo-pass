package com.cielo.cielopass.features.splash.domain.model

import com.cielo.cielopass.core.constants.SplashConstants.MSG_SYSTEM_READY

sealed interface SplashStep {
    val message: String

    data class Progress(
        override val message: String,
    ) : SplashStep

    data class Completed(
        override val message: String = MSG_SYSTEM_READY,
        val config: SplashConfig = SplashConfig(isInitialized = true),
    ) : SplashStep
}
