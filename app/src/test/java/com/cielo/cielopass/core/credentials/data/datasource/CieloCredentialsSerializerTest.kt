package com.cielo.cielopass.core.credentials.data.datasource

import androidx.datastore.core.CorruptionException
import com.cielo.cielopass.core.credentials.CieloCredentialsProto
import com.google.crypto.tink.Aead
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.GeneralSecurityException

class CieloCredentialsSerializerTest {
    private val aead: Aead = mockk()
    private lateinit var serializer: CieloCredentialsSerializer

    @Before
    fun setUp() {
        serializer = CieloCredentialsSerializer(aead)
    }

    @Test
    fun `defaultValue should return default instance`() {
        // Given / When
        val defaultProto = serializer.defaultValue

        // Then
        assertNotNull(defaultProto)
        assertEquals("", defaultProto.clientId)
        assertEquals("", defaultProto.accessToken)
    }

    @Test
    fun `writeTo should encrypt proto and write to output stream`() =
        runBlocking {
            // Given
            val proto = CieloCredentialsProto
                .newBuilder()
                .setClientId("client_123")
                .setAccessToken("token_abc")
                .build()
            val rawBytes = proto.toByteArray()
            val encryptedBytes = "encrypted_bytes".toByteArray()
            every { aead.encrypt(rawBytes, null) } returns encryptedBytes
            val outputStream = ByteArrayOutputStream()

            // When
            serializer.writeTo(proto, outputStream)

            // Then
            assertEquals(encryptedBytes.toList(), outputStream.toByteArray().toList())
        }

    @Test
    fun `readFrom should decrypt input stream and parse proto`() =
        runBlocking {
            // Given
            val originalProto = CieloCredentialsProto
                .newBuilder()
                .setClientId("client_123")
                .setAccessToken("token_abc")
                .build()
            val encryptedBytes = "encrypted_payload".toByteArray()
            val decryptedBytes = originalProto.toByteArray()
            every { aead.decrypt(encryptedBytes, null) } returns decryptedBytes
            val inputStream = ByteArrayInputStream(encryptedBytes)

            // When
            val resultProto = serializer.readFrom(inputStream)

            // Then
            assertEquals("client_123", resultProto.clientId)
            assertEquals("token_abc", resultProto.accessToken)
        }

    @Test(expected = CorruptionException::class)
    fun `readFrom should throw CorruptionException when decryption fails`() =
        runBlocking {
            // Given
            val encryptedBytes = "bad_bytes".toByteArray()
            every { aead.decrypt(encryptedBytes, null) } throws GeneralSecurityException("Decryption failed")
            val inputStream = ByteArrayInputStream(encryptedBytes)

            // When / Then
            serializer.readFrom(inputStream)
            Unit
        }

    @Test(expected = CorruptionException::class)
    fun `readFrom should throw CorruptionException when stream is empty`() =
        runBlocking {
            // Given
            val inputStream = ByteArrayInputStream(byteArrayOf())

            // When / Then
            serializer.readFrom(inputStream)
            Unit
        }
}
