package com.example.androsubmis2.setting

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.work.ExistingPeriodicWorkPolicy


fun scheduleReminder(context: Context) {
    val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyWorker>(1, TimeUnit.DAYS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "DailyEventReminder",
        ExistingPeriodicWorkPolicy.KEEP,
        dailyWorkRequest
    )
}