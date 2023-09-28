package com.example.daggerexamplemvvm.di

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    private var baseUrl = "https://api.nasa.gov/"

    @Singleton
    @Provides
    fun getRetroServiceInterface(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun getRetrofitInstance(): Retrofit {
        // Create a Retrofit instance
        return Retrofit.Builder()
            .baseUrl(baseUrl) // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create()) // Add a converter factory (e.g., Gson)
            .build()
    }


}