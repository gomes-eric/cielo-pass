package com.cielo.cielopass.features.splash.domain.model

import com.cielo.cielopass.BuildConfig

data class SplashConfig(
    val isInitialized: Boolean = false,
    val appVersion: String = BuildConfig.VERSION_NAME,
)
