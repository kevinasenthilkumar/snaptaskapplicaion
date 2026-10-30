package com.example.snaptask

import android.app.Application
import com.example.snaptask.di.AppContainer

class SnapTaskApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

