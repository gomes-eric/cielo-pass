package com.cielo.cielopass.features.splash.domain.usecase

import com.cielo.cielopass.BuildConfig
import com.cielo.cielopass.core.constants.SplashConstants.MSG_CHECKING_CREDENTIALS
import com.cielo.cielopass.core.constants.SplashConstants.MSG_CHECKING_PENDING_TRANSACTIONS
import com.cielo.cielopass.core.constants.SplashConstants.MSG_INITIALIZING_SYSTEM
import com.cielo.cielopass.core.constants.SplashConstants.MSG_SYSTEM_READY
import com.cielo.cielopass.core.constants.SplashConstants.SPLASH_STEP_DELAY_MS
import com.cielo.cielopass.core.credentials.domain.repository.CieloCredentialsRepository
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository
import com.cielo.cielopass.features.splash.domain.model.SplashConfig
import com.cielo.cielopass.features.splash.domain.model.SplashStep
import com.cielo.cielopass.features.splash.domain.model.SplashStep.Completed
import com.cielo.cielopass.features.splash.domain.model.SplashStep.Progress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration.Companion.milliseconds

class InitializeAppUseCase(
    private val credentialsRepository: CieloCredentialsRepository,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(): Flow<SplashStep> =
        flow {
            val clientId = BuildConfig.CIELO_CLIENT_ID
            val accessToken = BuildConfig.CIELO_ACCESS_TOKEN

            emit(Progress(MSG_INITIALIZING_SYSTEM))
            delay(SPLASH_STEP_DELAY_MS.milliseconds)

            emit(Progress(MSG_CHECKING_CREDENTIALS))

            if (clientId.isNotBlank() || accessToken.isNotBlank()) {
                credentialsRepository.saveCredentials(
                    clientId = clientId,
                    accessToken = accessToken,
                )
            } else {
                credentialsRepository.credentials.firstOrNull()
            }

            delay(SPLASH_STEP_DELAY_MS.milliseconds)

            emit(Progress(MSG_CHECKING_PENDING_TRANSACTIONS))
            delay(SPLASH_STEP_DELAY_MS.milliseconds)

            emit(
                Completed(
                    message = MSG_SYSTEM_READY,
                    config = SplashConfig(),
                ),
            )
        }.flowOn(Dispatchers.IO)
}
