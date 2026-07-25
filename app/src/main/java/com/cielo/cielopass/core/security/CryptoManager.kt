package com.cielo.cielopass.core.security

import android.content.Context
import com.cielo.cielopass.core.constants.SecurityConstants.KEYSET_NAME
import com.cielo.cielopass.core.constants.SecurityConstants.KEY_TEMPLATE
import com.cielo.cielopass.core.constants.SecurityConstants.MASTER_KEY_URI
import com.cielo.cielopass.core.constants.SecurityConstants.PREF_FILE_NAME
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager

object CryptoManager {
    fun getAead(context: Context): Aead {
        AeadConfig.register()

        return AndroidKeysetManager
            .Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get(KEY_TEMPLATE))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }
}
