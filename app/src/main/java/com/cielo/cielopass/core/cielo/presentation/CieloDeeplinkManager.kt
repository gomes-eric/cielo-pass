package com.cielo.cielopass.core.cielo.presentation

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse
import com.cielo.cielopass.core.cielo.domain.usecase.ProcessCieloResponseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CieloDeeplinkManager(
    private val processCieloResponseUseCase: ProcessCieloResponseUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
) {
    private val _deeplinkResponse = MutableSharedFlow<CieloDeeplinkResponse>(
        replay = 1,
        extraBufferCapacity = 1,
    )
    val deeplinkResponse: SharedFlow<CieloDeeplinkResponse> = _deeplinkResponse.asSharedFlow()

    fun handleDeeplinkUri(uriString: String) {
        scope.launch {
            val response = processCieloResponseUseCase(uriString)

            _deeplinkResponse.emit(response)
        }
    }
}
