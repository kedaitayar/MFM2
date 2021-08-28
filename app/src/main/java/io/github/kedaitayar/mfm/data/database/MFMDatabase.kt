package io.github.kedaitayar.mfm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.kedaitayar.mfm.data.dao.AccountDao
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.dao.BudgetDao
import io.github.kedaitayar.mfm.data.dao.TransactionDao
import io.github.kedaitayar.mfm.data.entity.*

@Database(
    entities = [Account::class, Transaction::class, TransactionType::class, Budget::class, BudgetTransaction::class, BudgetType::class, BudgetDeadline::class],
    version = 9,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MFMDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun basicDao(): BasicDao

    companion object {
        private const val DATABASE_NAME: String = "mfm_db"

        @Volatile
        private var INSTANCE: MFMDatabase? = null

        fun getDatabase(context: Context): MFMDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MFMDatabase::class.java,
                    DATABASE_NAME
                )
//                    .addCallback(DatabaseCallback(scope))
//                    .fallbackToDestructiveMigration()
                    .createFromAsset("database/mfm_db.db")
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}