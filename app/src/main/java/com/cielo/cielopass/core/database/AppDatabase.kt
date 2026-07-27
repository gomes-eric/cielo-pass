package com.cielo.cielopass.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cielo.cielopass.core.database.converter.TransactionConverters
import com.cielo.cielopass.core.database.dao.EventDAO
import com.cielo.cielopass.core.database.dao.TransactionDAO
import com.cielo.cielopass.core.database.entity.EventEntity
import com.cielo.cielopass.core.database.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        EventEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(TransactionConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO

    abstract fun eventDAO(): EventDAO
}
