package com.cielo.cielopass.features.events.di

import com.cielo.cielopass.features.events.presentation.details.EventDetailsViewModel
import com.cielo.cielopass.features.events.presentation.list.EventListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val eventsModule = module {
    viewModelOf(::EventListViewModel)
    viewModelOf(::EventDetailsViewModel)
}
