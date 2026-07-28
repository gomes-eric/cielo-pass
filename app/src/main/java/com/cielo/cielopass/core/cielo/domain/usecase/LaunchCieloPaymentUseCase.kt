package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.cielo.domain.model.CieloDeeplink.Payment
import com.cielo.cielopass.core.cielo.domain.model.CieloPaymentRequest
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult.ActiveTransactionExists
import com.cielo.cielopass.core.cielo.domain.model.LaunchPaymentResult.Success
import com.cielo.cielopass.core.cielo.domain.repository.CieloDeeplinkRepository
import com.cielo.cielopass.core.constants.CieloConstants.MSG_ERROR_LAUNCHING_PAYMENT
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_FAILED
import com.cielo.cielopass.core.constants.CieloConstants.STATUS_PENDING
import com.cielo.cielopass.core.credentials.domain.repository.CieloCredentialsRepository
import com.cielo.cielopass.core.transaction.domain.model.Transaction
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class LaunchCieloPaymentUseCase(
    private val transactionRepository: TransactionRepository,
    private val cieloRepository: CieloDeeplinkRepository,
    private val credentialsRepository: CieloCredentialsRepository,
) {
    suspend operator fun invoke(request: CieloPaymentRequest): LaunchPaymentResult {
        val transactionId = UUID.randomUUID().toString()

        return try {
            val credentials = credentialsRepository.credentials.firstOrNull()
            val clientId = credentials?.clientId.takeUnless { it.isNullOrBlank() }.orEmpty()
            val accessToken = credentials?.accessToken.takeUnless { it.isNullOrBlank() }.orEmpty()

            val finalRequest = request.copy(
                clientId = clientId,
                accessToken = accessToken,
                reference = transactionId,
            )

            val transaction = Transaction(
                id = transactionId,
                amount = finalRequest.amount,
                status = STATUS_PENDING,
                eventId = finalRequest.eventId,
                quantity = finalRequest.quantity,
                inventoryDeducted = false,
            )
            val inserted = transactionRepository.insertIfNoPending(transaction)

            if (!inserted) {
                val activePending = transactionRepository.getPending()

                return ActiveTransactionExists(activePending?.id.orEmpty())
            }

            cieloRepository.launchDeeplink(Payment(finalRequest))

            Success
        } catch (e: Exception) {
            transactionRepository.updateStatus(transactionId, STATUS_FAILED)
            LaunchPaymentResult.Error(e.message ?: MSG_ERROR_LAUNCHING_PAYMENT)
        }
    }
}
