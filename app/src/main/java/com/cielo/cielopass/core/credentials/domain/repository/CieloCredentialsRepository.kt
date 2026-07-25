package com.cielo.cielopass.core.credentials.domain.repository

import com.cielo.cielopass.core.credentials.domain.model.CieloCredentials
import kotlinx.coroutines.flow.Flow

interface CieloCredentialsRepository {
    val credentials: Flow<CieloCredentials>
    val clientId: Flow<String>
    val accessToken: Flow<String>

    suspend fun saveCredentials(
        clientId: String,
        accessToken: String,
    )

    suspend fun updateClientId(clientId: String)

    suspend fun updateAccessToken(accessToken: String)

    suspend fun clearCredentials()
}
