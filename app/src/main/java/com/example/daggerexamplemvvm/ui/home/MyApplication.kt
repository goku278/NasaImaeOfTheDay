package com.example.daggerexamplemvvm.ui.home

import android.app.Application
import com.example.daggerexamplemvvm.di.ApplicationComponent
import com.example.daggerexamplemvvm.di.DaggerApplicationComponent
import com.example.daggerexamplemvvm.di.NetworkModule

class MyApplication : Application() {
    private lateinit var retroComponent: ApplicationComponent
    override fun onCreate() {
        super.onCreate()
        retroComponent = DaggerApplicationComponent.builder()
            .networkModule(NetworkModule())
            .build()

    }

    fun getRetroComponent(): ApplicationComponent {
        return retroComponent
    }
}