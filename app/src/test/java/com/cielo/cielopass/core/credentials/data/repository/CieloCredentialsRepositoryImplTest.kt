package com.cielo.cielopass.core.credentials.data.repository

import androidx.datastore.core.DataStore
import com.cielo.cielopass.core.credentials.CieloCredentialsProto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CieloCredentialsRepositoryImplTest {
    private lateinit var dataStore: DataStore<CieloCredentialsProto>
    private lateinit var repository: CieloCredentialsRepositoryImpl

    @Before
    fun setUp() {
        dataStore = mockk()
        val initialProto = CieloCredentialsProto
            .newBuilder()
            .setClientId("client-123")
            .setAccessToken("token-456")
            .build()
        coEvery { dataStore.data } returns flowOf(initialProto)
        repository = CieloCredentialsRepositoryImpl(dataStore)
    }

    @Test
    fun `given datastore credentials when credentials flow collected then emit domain model`() =
        runTest {
            // WHEN
            val credentials = repository.credentials.first()

            // THEN
            assertEquals("client-123", credentials.clientId)
            assertEquals("token-456", credentials.accessToken)
        }

    @Test
    fun `given new client and token when saveCredentials called then update datastore`() =
        runTest {
            // GIVEN
            coEvery { dataStore.updateData(any()) } returns CieloCredentialsProto.getDefaultInstance()

            // WHEN
            repository.saveCredentials("new-client", "new-token")

            // THEN
            coVerify(exactly = 1) { dataStore.updateData(any()) }
        }

    @Test
    fun `given clearCredentials called then clear values in datastore`() =
        runTest {
            // GIVEN
            coEvery { dataStore.updateData(any()) } returns CieloCredentialsProto.getDefaultInstance()

            // WHEN
            repository.clearCredentials()

            // THEN
            coVerify(exactly = 1) { dataStore.updateData(any()) }
        }
}
