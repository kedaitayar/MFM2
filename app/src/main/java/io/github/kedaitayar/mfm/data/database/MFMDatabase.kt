package io.github.kedaitayar.mfm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.kedaitayar.mfm.data.dao.AccountDao
import io.github.kedaitayar.mfm.data.dao.BudgetDao
import io.github.kedaitayar.mfm.data.dao.TransactionDao
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm2.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Account::class, Transaction::class, TransactionType::class, Budget::class, BudgetTransaction::class, BudgetType::class, BudgetDeadline::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MFMDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}