package com.cielo.cielopass.features.splash.di

import com.cielo.cielopass.features.splash.domain.usecase.InitializeAppUseCase
import com.cielo.cielopass.features.splash.presentation.SplashViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val splashModule = module {
    factoryOf(::InitializeAppUseCase)

    viewModelOf(::SplashViewModel)
}
