package com.takipsan.levinson.di

import android.content.Context
import com.takipsan.levinson.DataAccess.Room.db.LevinsonDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesNovaDatabase(@ApplicationContext context: Context): LevinsonDatabase {
        return LevinsonDatabase.databaseConnection(context)
            ?: throw IllegalStateException("NovaDatabase cannot be null.")
    }

    @Singleton
    @Provides
    fun providesConsigmentDao(novaDatabase: LevinsonDatabase) = novaDatabase.ConsigmentDao()

    @Singleton
    @Provides
    fun providesConsigmentEpcDao(novaDatabase: LevinsonDatabase) = novaDatabase.ConsigmentEpcDao()

}