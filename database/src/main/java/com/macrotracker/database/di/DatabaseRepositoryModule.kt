package com.macrotracker.database.di

import android.content.Context
import com.macrotracker.database.DatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseRepositoryModule {

    @Provides
    fun provideDatabaseRepository(@ApplicationContext appContext: Context): DatabaseRepository {
        return DatabaseRepository(appContext)
    }
}