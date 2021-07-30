package io.github.kedaitayar.mfm.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.github.kedaitayar.mfm.data.dao.AccountDao
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.dao.BudgetDao
import io.github.kedaitayar.mfm.data.dao.TransactionDao
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.BudgetType
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RoomModule::class]
)
class MockRoomModule {
    @Singleton
    @Provides
    fun provideMFMDatabase(): MFMDatabase {
//        return MFMDatabase.getDatabase(context)
        val database =
            Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), MFMDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        GlobalScope.launch {
            database.basicDao().apply {
                insert(BudgetType(budgetTypeId = 1, budgetTypeName = "Monthly"))
                insert(BudgetType(budgetTypeId = 2, budgetTypeName = "Yearly"))
                insert(TransactionType(transactionTypeId = 1, transactionTypeName = "Expense"))
                insert(TransactionType(transactionTypeId = 2, transactionTypeName = "Income"))
                insert(TransactionType(transactionTypeId = 3, transactionTypeName = "Transfer"))
                insert(Account(accountId = 1, accountName = "Cash"))
                insert(Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1))
            }
        }

        return database
    }

    @Provides
    fun provideAccountDao(mfmDatabase: MFMDatabase): AccountDao {
        return mfmDatabase.accountDao()
    }

    @Provides
    fun provideTransactionDao(mfmDatabase: MFMDatabase): TransactionDao {
        return mfmDatabase.transactionDao()
    }

    @Provides
    fun provideBudgetDao(mfmDatabase: MFMDatabase): BudgetDao {
        return mfmDatabase.budgetDao()
    }

    @Provides
    fun provideBasicDao(mfmDatabase: MFMDatabase): BasicDao {
        return mfmDatabase.basicDao()
    }

    @Singleton
    @Provides
    fun provideSelectedDateRepo(): SelectedDateRepository {
        return SelectedDateRepository()
    }
}