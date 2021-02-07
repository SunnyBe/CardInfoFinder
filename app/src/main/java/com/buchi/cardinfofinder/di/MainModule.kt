package com.buchi.cardinfofinder.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.buchi.cardinfofinder.data.network.ApiService
import com.buchi.cardinfofinder.data.repository.MainRepository
import com.buchi.cardinfofinder.data.repository.MainRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
abstract class MainViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@Module
@InstallIn(ActivityComponent::class)
object MainModule {
    @Singleton
    @Provides
    fun provideApiService(): ApiService = Retrofit.Builder()
        .baseUrl("https://stackoverflw.herokuapp.com/v1/")
        .build()
        .create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideMainRepository(
        @ApplicationContext context: Context,
        apiService: ApiService
    ): MainRepository = MainRepositoryImpl(context, apiService)
}