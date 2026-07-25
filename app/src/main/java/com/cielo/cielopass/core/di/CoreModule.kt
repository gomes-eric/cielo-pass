package com.cielo.cielopass.core.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.cielo.cielopass.core.cielo.data.builder.CieloDeeplinkBuilder
import com.cielo.cielopass.core.cielo.data.parser.CieloResponseParser
import com.cielo.cielopass.core.cielo.data.repository.CieloDeeplinkRepositoryImpl
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import com.cielo.cielopass.core.cielo.domain.usecase.CheckActiveTransactionUseCase
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloEnabledProductsUseCase
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloEstablishmentsUseCase
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloOrderQueryUseCase
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloOrdersListUseCase
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloPaymentUseCase
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloPrintUseCase
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloReversalUseCase
import com.cielo.cielopass.core.cielo.domain.usecase.LaunchCieloTerminalInfoUseCase
import com.cielo.cielopass.core.cielo.domain.usecase.ProcessCieloResponseUseCase
import com.cielo.cielopass.core.cielo.presentation.CieloDeeplinkManager
import com.cielo.cielopass.core.constants.DataStoreConstants.CREDENTIALS_FILE_NAME
import com.cielo.cielopass.core.constants.DatabaseConstants.DB_NAME
import com.cielo.cielopass.core.credentials.data.datasource.CieloCredentialsSerializer
import com.cielo.cielopass.core.credentials.data.repository.CieloCredentialsRepositoryImpl
import com.cielo.cielopass.core.credentials.domain.repository.CieloCredentialsRepository
import com.cielo.cielopass.core.database.AppDatabase
import com.cielo.cielopass.core.event.data.repository.EventRepositoryImpl
import com.cielo.cielopass.core.event.domain.repository.EventRepository
import com.cielo.cielopass.core.security.CryptoManager
import com.cielo.cielopass.core.transaction.data.repository.TransactionRepositoryImpl
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    // DataStore for Cielo Credentials
    single {
        val context = androidContext()
        val aead = CryptoManager.getAead(context)

        DataStoreFactory.create(
            serializer = CieloCredentialsSerializer(aead),
            produceFile = { context.dataStoreFile(CREDENTIALS_FILE_NAME) },
        )
    }
    single<CieloCredentialsRepository> { CieloCredentialsRepositoryImpl(get()) }

    // Room Database
    single {
        Room
            .databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                DB_NAME,
            ).fallbackToDestructiveMigration(false)
            .build()
    }
    single { get<AppDatabase>().transactionDAO() }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single { get<AppDatabase>().eventDAO() }
    single<EventRepository> { EventRepositoryImpl(get()) }

    // Cielo Deeplink Components
    single { CieloResponseParser() }
    single { CieloDeeplinkBuilder() }
    single<CieloDeeplinkRepository> { CieloDeeplinkRepositoryImpl(androidContext(), get(), get()) }

    // Use Cases
    single { CheckActiveTransactionUseCase(get()) }
    single { LaunchCieloPaymentUseCase(get(), get(), get()) }
    single { LaunchCieloReversalUseCase(get()) }
    single { LaunchCieloPrintUseCase(get()) }
    single { LaunchCieloTerminalInfoUseCase(get()) }
    single { LaunchCieloOrdersListUseCase(get()) }
    single { LaunchCieloOrderQueryUseCase(get()) }
    single { LaunchCieloEnabledProductsUseCase(get()) }
    single { LaunchCieloEstablishmentsUseCase(get()) }
    single { ProcessCieloResponseUseCase(get(), get()) }

    // Presentation / Deeplink Manager
    single { CieloDeeplinkManager(get()) }
}
