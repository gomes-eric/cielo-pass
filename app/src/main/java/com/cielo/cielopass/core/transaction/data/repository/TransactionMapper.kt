package com.cielo.cielopass.core.transaction.data.repository

import com.cielo.cielopass.core.database.entity.TransactionEntity
import com.cielo.cielopass.core.transaction.domain.model.Transaction

fun TransactionEntity.toDomain(): Transaction =
    Transaction(
        id = id,
        orderId = orderId,
        status = status,
        amount = amount,
        items = items,
        payments = payments,
        rawResponse = rawResponse,
        errorMessage = errorMessage,
        eventId = eventId,
        quantity = quantity,
        inventoryDeducted = inventoryDeducted,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Transaction.toEntity(): TransactionEntity =
    TransactionEntity(
        id = id,
        orderId = orderId,
        status = status,
        amount = amount,
        items = items,
        payments = payments,
        rawResponse = rawResponse,
        errorMessage = errorMessage,
        eventId = eventId,
        quantity = quantity,
        inventoryDeducted = inventoryDeducted,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
