package com.cielo.cielopass.core.credentials.data.repository

import androidx.datastore.core.DataStore
import com.cielo.cielopass.core.credentials.CieloCredentialsProto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CieloCredentialsRepositoryImplTest {
    private val dataStore: DataStore<CieloCredentialsProto> = mockk()
    private lateinit var repository: CieloCredentialsRepositoryImpl

    @Before
    fun setUp() {
        every { dataStore.data } returns flowOf(CieloCredentialsProto.getDefaultInstance())
        repository = CieloCredentialsRepositoryImpl(dataStore)
    }

    @Test
    fun `credentials flow emits mapped domain model from DataStore`() =
        runBlocking {
            // Given
            val proto = CieloCredentialsProto
                .newBuilder()
                .setClientId("client_123")
                .setAccessToken("token_abc")
                .build()
            every { dataStore.data } returns flowOf(proto)
            repository = CieloCredentialsRepositoryImpl(dataStore)

            // When
            val result = repository.credentials.first()

            // Then
            assertEquals("client_123", result.clientId)
            assertEquals("token_abc", result.accessToken)
        }

    @Test
    fun `clientId flow emits clientId from DataStore`() =
        runBlocking {
            // Given
            val proto = CieloCredentialsProto
                .newBuilder()
                .setClientId("client_123")
                .build()
            every { dataStore.data } returns flowOf(proto)
            repository = CieloCredentialsRepositoryImpl(dataStore)

            // When
            val clientId = repository.clientId.first()

            // Then
            assertEquals("client_123", clientId)
        }

    @Test
    fun `accessToken flow emits accessToken from DataStore`() =
        runBlocking {
            // Given
            val proto = CieloCredentialsProto
                .newBuilder()
                .setAccessToken("token_abc")
                .build()
            every { dataStore.data } returns flowOf(proto)
            repository = CieloCredentialsRepositoryImpl(dataStore)

            // When
            val accessToken = repository.accessToken.first()

            // Then
            assertEquals("token_abc", accessToken)
        }

    @Test
    fun `saveCredentials updates DataStore`() =
        runBlocking {
            // Given
            val initialProto = CieloCredentialsProto.getDefaultInstance()
            coEvery { dataStore.updateData(any()) } answers {
                val transform = firstArg<suspend (CieloCredentialsProto) -> CieloCredentialsProto>()
                runBlocking { transform(initialProto) }
            }

            // When
            repository.saveCredentials("id_test", "token_test")

            // Then
            coVerify(exactly = 1) { dataStore.updateData(any()) }
        }

    @Test
    fun `clearCredentials clears DataStore`() =
        runBlocking {
            // Given
            val initialProto = CieloCredentialsProto
                .newBuilder()
                .setClientId("client")
                .setAccessToken("token")
                .build()
            coEvery { dataStore.updateData(any()) } answers {
                val transform = firstArg<suspend (CieloCredentialsProto) -> CieloCredentialsProto>()
                runBlocking { transform(initialProto) }
            }

            // When
            repository.clearCredentials()

            // Then
            coVerify(exactly = 1) { dataStore.updateData(any()) }
        }
}
