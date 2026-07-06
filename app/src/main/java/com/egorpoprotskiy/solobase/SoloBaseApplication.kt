package com.egorpoprotskiy.solobase

import android.app.Application
import com.egorpoprotskiy.solobase.data.reminder.TaskNotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SoloBaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        TaskNotificationHelper.createNotificationChannel(this)
    }
}
