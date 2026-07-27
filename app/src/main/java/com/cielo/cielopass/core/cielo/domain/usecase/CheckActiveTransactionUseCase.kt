package com.cielo.cielopass.core.cielo.domain.usecase

import com.cielo.cielopass.core.transaction.domain.model.Transaction
import com.cielo.cielopass.core.transaction.domain.repository.TransactionRepository

class CheckActiveTransactionUseCase(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(): Transaction? = transactionRepository.getPending()
}
