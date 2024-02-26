package com.example.parkingqr.di

import com.example.parkingqr.data.IRepository
import com.example.parkingqr.data.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindRepository(repository: Repository): IRepository
}