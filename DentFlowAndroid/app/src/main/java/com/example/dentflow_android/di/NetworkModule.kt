package com.example.dentflow_android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.example.dentflow_android.data.remote.ApiService
import com.example.dentflow_android.data.remote.AuthService
import javax.inject.Named
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("auth_retrofit")
    fun provideAuthRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8081/") // Tu bije serce logowania
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("core_retrofit")
    fun provideCoreRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/") // Tu są wizyty i pacjenci
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideAuthService(@Named("auth_retrofit") retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiService(@Named("core_retrofit") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}