package com.cielo.cielopass.core.credentials.data.datasource

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.cielo.cielopass.core.credentials.CieloCredentialsProto
import com.google.crypto.tink.Aead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

class CieloCredentialsSerializer(
    private val aead: Aead,
) : Serializer<CieloCredentialsProto> {
    override val defaultValue: CieloCredentialsProto = CieloCredentialsProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CieloCredentialsProto =
        withContext(Dispatchers.IO) {
            runCatching {
                val encryptedBytes = input.readBytes()
                check(encryptedBytes.isNotEmpty()) { "Encrypted bytes stream is empty" }
                val decryptedBytes = aead.decrypt(encryptedBytes, null)

                CieloCredentialsProto.parseFrom(decryptedBytes)
            }.getOrElse { exception ->
                throw CorruptionException("Cannot read or decrypt credentials proto.", exception)
            }
        }

    override suspend fun writeTo(
        t: CieloCredentialsProto,
        output: OutputStream,
    ) = withContext(Dispatchers.IO) {
        val encryptedBytes = aead.encrypt(t.toByteArray(), null)

        output.write(encryptedBytes)
    }
}
