package com.vakifbank.WatchWise

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WatchWiseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}