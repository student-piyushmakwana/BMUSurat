package com.piyush.bmusurat.di

import android.content.Context
import androidx.room.Room
import com.piyush.bmusurat.data.local.AppDatabase
import com.piyush.bmusurat.data.local.HomeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBmuDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bmu_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBmuDao(database: AppDatabase): HomeDao {
        return database.homeDao()
    }
}