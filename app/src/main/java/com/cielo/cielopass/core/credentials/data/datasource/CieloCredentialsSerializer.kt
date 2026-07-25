package com.cielo.cielopass.core.credentials.data.datasource

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.cielo.cielopass.core.constants.DataStoreConstants.ERR_DECRYPT_CREDENTIALS_PROTO
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
            val encryptedBytes = input.readBytes()

            if (encryptedBytes.isEmpty()) return@withContext defaultValue

            runCatching {
                val decryptedBytes = aead.decrypt(encryptedBytes, null)

                CieloCredentialsProto.parseFrom(decryptedBytes)
            }.getOrElse { exception ->
                throw CorruptionException(ERR_DECRYPT_CREDENTIALS_PROTO, exception)
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
