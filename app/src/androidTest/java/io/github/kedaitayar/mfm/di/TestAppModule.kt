package io.github.kedaitayar.mfm.di

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class TestAppModule {

    @Named("test_database")
    @Provides
    fun provideInMemoryDatabase(@ApplicationContext context: Context): MFMDatabase {
        return Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), MFMDatabase::class.java)
            .allowMainThreadQueries()
//            .createFromAsset("database/mfm_db.db")
            .build()
    }
}