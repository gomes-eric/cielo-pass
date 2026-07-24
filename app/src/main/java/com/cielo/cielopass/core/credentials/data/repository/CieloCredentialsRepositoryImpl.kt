package com.cielo.cielopass.core.credentials.data.repository

import androidx.datastore.core.DataStore
import com.cielo.cielopass.core.credentials.CieloCredentialsProto
import com.cielo.cielopass.core.credentials.domain.model.CieloCredentials
import com.cielo.cielopass.core.credentials.domain.repository.CieloCredentialsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CieloCredentialsRepositoryImpl(
    private val dataStore: DataStore<CieloCredentialsProto>,
) : CieloCredentialsRepository {
    override val credentials: Flow<CieloCredentials> =
        dataStore.data
            .map { proto ->
                CieloCredentials(
                    clientId = proto.clientId,
                    accessToken = proto.accessToken,
                )
            }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override val clientId: Flow<String> = credentials.map { it.clientId }.distinctUntilChanged().flowOn(Dispatchers.IO)

    override val accessToken: Flow<String> = credentials.map { it.accessToken }.distinctUntilChanged().flowOn(Dispatchers.IO)

    override suspend fun saveCredentials(
        clientId: String,
        accessToken: String,
    ) = withContext(Dispatchers.IO) {
        dataStore.updateData { currentCredentials ->
            currentCredentials
                .toBuilder()
                .setClientId(clientId)
                .setAccessToken(accessToken)
                .build()
        }
        Unit
    }

    override suspend fun updateClientId(clientId: String) =
        withContext(Dispatchers.IO) {
            dataStore.updateData { currentCredentials ->
                currentCredentials
                    .toBuilder()
                    .setClientId(clientId)
                    .build()
            }
            Unit
        }

    override suspend fun updateAccessToken(accessToken: String) =
        withContext(Dispatchers.IO) {
            dataStore.updateData { currentCredentials ->
                currentCredentials
                    .toBuilder()
                    .setAccessToken(accessToken)
                    .build()
            }
            Unit
        }

    override suspend fun clearCredentials() =
        withContext(Dispatchers.IO) {
            dataStore.updateData { currentCredentials ->
                currentCredentials
                    .toBuilder()
                    .clearClientId()
                    .clearAccessToken()
                    .build()
            }
            Unit
        }
}
