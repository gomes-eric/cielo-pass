package com.cielo.cielopass.features.checkout.di

import com.cielo.cielopass.features.checkout.presentation.CheckoutViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val checkoutModule = module {
    viewModelOf(::CheckoutViewModel)
}
