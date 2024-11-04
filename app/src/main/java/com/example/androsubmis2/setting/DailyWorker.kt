package com.example.androsubmis2.setting

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.androsubmis2.R
import com.example.androsubmis2.models.EventModel
import com.example.androsubmis2.service.EventRepository
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DailyWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val eventRepository: EventRepository by inject()
    private val themePreferenceManager: ThemePreferenceManager by inject()

    override suspend fun doWork(): Result {
        if (!areNotificationsEnabled()) {
            return Result.success()
        }

        val nearestEvent: EventModel? = try {
            val events = eventRepository.getNearestActiveEvent()
            events.firstOrNull()
        } catch (e: Exception) {
            null
        }

        nearestEvent?.let {
            sendNotification(it.name ?: "Upcoming Event", it.beginTime ?: "No time available")
        }

        return Result.success()
    }

    private fun sendNotification(eventName: String, eventTime: String) {
        if (!checkNotificationPermission()) return

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "daily_reminder",
                "Daily Event Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminder for the nearest upcoming event."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "daily_reminder")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Event Reminder")
            .setContentText("$eventName at $eventTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private suspend fun areNotificationsEnabled(): Boolean {
        return themePreferenceManager.isNotificationEnabled.first()
    }
}
