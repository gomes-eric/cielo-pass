package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Payment
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.TerminalError
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Approved
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Cancelled
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Failed
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_APPROVED
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_CANCELLED
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_FAILED
import com.cielo.cielopass.core.transaction.domain.model.TransactionItem
import com.cielo.cielopass.core.transaction.domain.model.TransactionPayment
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository
import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplinkResponse.Unknown as UnknownResponse
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentResult.Unknown as UnknownResult

class ProcessCieloResponseUseCase(
    private val cieloRepository: CieloDeeplinkRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(uriString: String): CieloDeeplinkResponse {
        cieloRepository.stopPaymentService()

        val response = cieloRepository.parseResponseUri(uriString)

        when (response) {
            is Payment -> {
                when (val result = response.result) {
                    is Approved -> {
                        updateTransactionPendingStatus(result.orderId, STATUS_APPROVED, result, null, result.reference)
                    }

                    is Cancelled -> {
                        updateTransactionPendingStatus("", STATUS_CANCELLED, null, result.reason)
                    }

                    is Failed -> {
                        updateTransactionPendingStatus("", STATUS_FAILED, null, result.reason)
                    }

                    is UnknownResult -> {
                        cancelPendingTransaction()
                    }
                }
            }

            is TerminalError -> {
                cancelPendingTransaction(response.message)
            }

            is UnknownResponse -> {
                cancelPendingTransaction()
            }

            else -> {
                cancelPendingTransaction()
            }
        }

        return response
    }

    private suspend fun updateTransactionPendingStatus(
        orderId: String,
        status: String,
        approvedResult: Approved? = null,
        errorMessage: String? = null,
        reference: String? = null,
    ) {
        val pending = (reference?.takeIf { it.isNotBlank() }?.let { transactionRepository.getById(it) }) ?: transactionRepository.getPending() ?: return
        val items = approvedResult?.items?.map { TransactionItem(id = it.id, sku = it.sku) }
        val payments = approvedResult?.payments?.map { TransactionPayment(id = it.id, authCode = it.authCode, nsu = it.nsu) }

        val updated = pending.copy(
            orderId = orderId.ifBlank { pending.orderId },
            status = status,
            amount = approvedResult?.amount ?: pending.amount,
            items = items?.takeIf { it.isNotEmpty() } ?: pending.items,
            payments = payments?.takeIf { it.isNotEmpty() } ?: pending.payments,
            rawResponse = approvedResult?.rawResponse ?: pending.rawResponse,
            errorMessage = errorMessage ?: pending.errorMessage,
            updatedAt = System.currentTimeMillis(),
        )

        transactionRepository.update(updated)
    }

    private suspend fun cancelPendingTransaction(error: String? = null) {
        val pendingId = transactionRepository.getPending()?.id

        if (!pendingId.isNullOrBlank()) {
            updateTransactionPendingStatus("", STATUS_FAILED, null, error)
        }
    }
}
