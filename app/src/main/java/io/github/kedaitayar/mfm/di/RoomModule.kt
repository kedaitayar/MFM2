package io.github.kedaitayar.mfm.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.kedaitayar.mfm.data.dao.*
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RoomModule {

    @Singleton
    @Provides
    fun provideMFMDatabase(@ApplicationContext context: Context): MFMDatabase {
        return MFMDatabase.getDatabase(context)
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

    @Provides
    fun provideQuickTransactionDao(mfmDatabase: MFMDatabase): QuickTransactionDao {
        return mfmDatabase.quickTransactionDao()
    }

    @Singleton
    @Provides
    fun provideSelectedDateRepo(): SelectedDateRepository {
        return SelectedDateRepository()
    }
}