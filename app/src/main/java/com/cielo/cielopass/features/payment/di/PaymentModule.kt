package com.cielo.cielopass.features.payment.di

import com.cielo.cielopass.features.payment.presentation.PaymentResultViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val paymentModule = module {
    viewModelOf(::PaymentResultViewModel)
}
