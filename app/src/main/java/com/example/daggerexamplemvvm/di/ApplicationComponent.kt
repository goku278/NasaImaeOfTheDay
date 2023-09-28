package com.example.daggerexamplemvvm.di

import com.example.daggerexamplemvvm.ui.home.MainActivityViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface ApplicationComponent {
    fun inject(mainActivityViewModel: MainActivityViewModel)
}