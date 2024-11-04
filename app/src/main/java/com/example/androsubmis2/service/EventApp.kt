package com.example.androsubmis2.service

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EventApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EventApp)
            modules(appModule)
        }
    }
}
