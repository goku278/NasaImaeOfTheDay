package com.example.daggerexamplemvvm.di

import com.example.daggerexamplemvvm.model.NasaImageApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("planetary/apod")
    fun getImage(@Query("api_key") apiKey: String): Call<NasaImageApiResponse>
}