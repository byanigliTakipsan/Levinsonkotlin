package com.takipsan.levinson.di

import com.pda.rfid.uhf.UHFReader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HopeLandReader {
    @Singleton
    @Provides
   fun GetANTPowerParam():Int {
        return UHFReader._Config.GetANTPowerParam()
   }



}