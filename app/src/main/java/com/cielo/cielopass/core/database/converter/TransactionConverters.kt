package com.cielo.cielopass.core.database.converter

import androidx.room.TypeConverter
import com.cielo.cielopass.core.transaction.domain.model.TransactionItem
import com.cielo.cielopass.core.transaction.domain.model.TransactionPayment
import kotlinx.serialization.json.Json

class TransactionConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromItemList(value: List<TransactionItem>?): String = if (value.isNullOrEmpty()) "" else json.encodeToString(value)

    @TypeConverter
    fun toItemList(value: String?): List<TransactionItem> =
        if (value.isNullOrBlank()) {
            emptyList()
        } else {
            try {
                json.decodeFromString(value)
            } catch (_: Exception) {
                emptyList()
            }
        }

    @TypeConverter
    fun fromPaymentList(value: List<TransactionPayment>?): String = if (value.isNullOrEmpty()) "" else json.encodeToString(value)

    @TypeConverter
    fun toPaymentList(value: String?): List<TransactionPayment> =
        if (value.isNullOrBlank()) {
            emptyList()
        } else {
            try {
                json.decodeFromString(value)
            } catch (_: Exception) {
                emptyList()
            }
        }
}
