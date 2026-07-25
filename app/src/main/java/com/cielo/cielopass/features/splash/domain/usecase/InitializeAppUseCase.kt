package com.cielo.cielopass.features.splash.domain.usecase

import com.cielo.cielopass.BuildConfig
import com.cielo.cielopass.core.constants.SplashConstants.MSG_CHECKING_CREDENTIALS
import com.cielo.cielopass.core.constants.SplashConstants.MSG_CHECKING_PENDING_TRANSACTIONS
import com.cielo.cielopass.core.constants.SplashConstants.MSG_INITIALIZING_SYSTEM
import com.cielo.cielopass.core.constants.SplashConstants.MSG_SYSTEM_READY
import com.cielo.cielopass.core.constants.SplashConstants.SPLASH_STEP_DELAY_MS
import com.cielo.cielopass.core.credentials.domain.repository.CieloCredentialsRepository
import com.cielo.cielopass.features.splash.domain.model.SplashConfig
import com.cielo.cielopass.features.splash.domain.model.SplashStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration.Companion.milliseconds

class InitializeAppUseCase(
    private val credentialsRepository: CieloCredentialsRepository,
) {
    operator fun invoke(): Flow<SplashStep> =
        flow {
            emit(SplashStep.Progress(MSG_INITIALIZING_SYSTEM))
            delay(SPLASH_STEP_DELAY_MS.milliseconds)

            emit(SplashStep.Progress(MSG_CHECKING_CREDENTIALS))
            if (BuildConfig.CIELO_CLIENT_ID.isNotBlank() || BuildConfig.CIELO_ACCESS_TOKEN.isNotBlank()) {
                credentialsRepository.saveCredentials(
                    clientId = BuildConfig.CIELO_CLIENT_ID,
                    accessToken = BuildConfig.CIELO_ACCESS_TOKEN,
                )
            } else {
                credentialsRepository.credentials.firstOrNull()
            }
            delay(SPLASH_STEP_DELAY_MS.milliseconds)

            emit(SplashStep.Progress(MSG_CHECKING_PENDING_TRANSACTIONS))
            delay(SPLASH_STEP_DELAY_MS.milliseconds)

            emit(
                SplashStep.Completed(
                    message = MSG_SYSTEM_READY,
                    config = SplashConfig(isInitialized = true),
                ),
            )
        }.flowOn(Dispatchers.IO)
}
