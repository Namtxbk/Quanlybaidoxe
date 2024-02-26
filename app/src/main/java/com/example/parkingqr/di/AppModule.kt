package com.example.parkingqr.di

import android.content.Context
import com.example.parkingqr.data.local.ILocalData
import com.example.parkingqr.data.local.LocalData
import com.example.parkingqr.data.remote.IRemoteDataSource
import com.example.parkingqr.data.remote.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideLocalRepository(@ApplicationContext context: Context): ILocalData{
        return LocalData(context)
    }

    @Provides
    @Singleton
    fun provideRemoteRepository(@ApplicationContext context: Context): IRemoteDataSource{
        return RemoteDataSource(context)
    }
}