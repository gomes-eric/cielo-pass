package com.cielo.cielopass.features.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cielo.cielopass.core.constants.SplashConstants.MSG_INITIALIZING_SYSTEM
import com.cielo.cielopass.core.constants.SplashConstants.MSG_UNKNOWN_INITIALIZATION_ERROR
import com.cielo.cielopass.features.splash.domain.model.SplashStep.Completed
import com.cielo.cielopass.features.splash.domain.model.SplashStep.Progress
import com.cielo.cielopass.features.splash.domain.usecase.InitializeAppUseCase
import com.cielo.cielopass.features.splash.presentation.SplashEffect.NavigateToEvents
import com.cielo.cielopass.features.splash.presentation.SplashEvent.Init
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val initializeAppUseCase: InitializeAppUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(Init)
    }

    fun onEvent(event: SplashEvent) {
        when (event) {
            Init -> initialize()
        }
    }

    private fun initialize() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    statusText = MSG_INITIALIZING_SYSTEM,
                    error = null,
                )
            }

            initializeAppUseCase()
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.localizedMessage ?: MSG_UNKNOWN_INITIALIZATION_ERROR,
                        )
                    }
                }.collect { step ->
                    when (step) {
                        is Progress -> {
                            _state.update {
                                it.copy(
                                    isLoading = true,
                                    statusText = step.message,
                                )
                            }
                        }

                        is Completed -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    config = step.config,
                                    statusText = step.message,
                                )
                            }
                            _effect.send(NavigateToEvents)
                        }
                    }
                }
        }
    }
}
