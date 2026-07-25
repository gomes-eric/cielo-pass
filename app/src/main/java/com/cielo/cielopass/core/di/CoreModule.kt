package com.cielo.cielopass.core.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.cielo.cielopass.core.constants.DataStoreConstants
import com.cielo.cielopass.core.credentials.data.datasource.CieloCredentialsSerializer
import com.cielo.cielopass.core.credentials.data.repository.CieloCredentialsRepositoryImpl
import com.cielo.cielopass.core.credentials.domain.repository.CieloCredentialsRepository
import com.cielo.cielopass.core.security.CryptoManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single {
        val context = androidContext()
        val aead = CryptoManager.getAead(context)

        DataStoreFactory.create(
            serializer = CieloCredentialsSerializer(aead),
            produceFile = { context.dataStoreFile(DataStoreConstants.CREDENTIALS_FILE_NAME) },
        )
    }
    single<CieloCredentialsRepository> { CieloCredentialsRepositoryImpl(get()) }
}
