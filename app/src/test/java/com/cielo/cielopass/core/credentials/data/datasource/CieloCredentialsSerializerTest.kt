package com.cielo.cielopass.core.credentials.data.datasource

import com.cielo.cielopass.core.credentials.CieloCredentialsProto
import com.google.crypto.tink.Aead
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class CieloCredentialsSerializerTest {
    private lateinit var aead: Aead
    private lateinit var serializer: CieloCredentialsSerializer

    @Before
    fun setUp() {
        aead = mockk()
        serializer = CieloCredentialsSerializer(aead)
    }

    @Test
    fun `given proto object when writeTo and readFrom then correctly encrypt and decrypt proto`() =
        runTest {
            // GIVEN
            val originalProto = CieloCredentialsProto
                .newBuilder()
                .setClientId("client-xyz")
                .setAccessToken("token-abc")
                .build()

            val plainBytes = originalProto.toByteArray()
            val fakeEncryptedBytes = byteArrayOf(1, 2, 3, 4)

            every { aead.encrypt(plainBytes, null) } returns fakeEncryptedBytes
            every { aead.decrypt(fakeEncryptedBytes, null) } returns plainBytes

            // WHEN (writeTo)
            val outputStream = ByteArrayOutputStream()
            serializer.writeTo(originalProto, outputStream)

            // THEN (readFrom)
            val inputStream = ByteArrayInputStream(outputStream.toByteArray())
            val readProto = serializer.readFrom(inputStream)

            assertEquals("client-xyz", readProto.clientId)
            assertEquals("token-abc", readProto.accessToken)
        }

    @Test
    fun `given empty input stream when readFrom then return default proto value`() =
        runTest {
            // GIVEN
            val emptyInputStream = ByteArrayInputStream(byteArrayOf())

            // WHEN
            val proto = serializer.readFrom(emptyInputStream)

            // THEN
            assertEquals("", proto.clientId)
            assertEquals("", proto.accessToken)
        }
}
