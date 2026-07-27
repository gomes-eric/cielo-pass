package com.cielo.cielopass

import android.app.Application
import com.cielo.cielopass.core.di.coreModule
import com.cielo.cielopass.features.events.di.eventsModule
import com.cielo.cielopass.features.splash.di.splashModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CieloPassApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CieloPassApplication)

            modules(
                listOf(
                    coreModule,
                    splashModule,
                    eventsModule,
                ),
            )
        }
    }
}
