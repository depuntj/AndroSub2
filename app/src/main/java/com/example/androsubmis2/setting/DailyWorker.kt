package com.example.androsubmis2.datastore

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.androsubmis2.MainActivity
import com.example.androsubmis2.R
import com.example.androsubmis2.service.EventRepository
import com.example.androsubmis2.service.RetrofitInstance

class DailyWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Fetch nearest active event
        val eventRepository = EventRepository(RetrofitInstance.eventService)
        val nearestEvent = eventRepository.getActiveEvents(limit = 1).firstOrNull()

        nearestEvent?.let {
            // Show notification for the nearest event
            showNotification(it.name ?: "Unknown Event", it.beginTime ?: "Unknown Time")
        }

        return Result.success()
    }

    private fun showNotification(eventName: String, eventTime: String) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Create notification channel if on Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "DAILY_REMINDER",
                "Daily Event Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open the app when notification is tapped
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(applicationContext, "DAILY_REMINDER")
            .setContentTitle("Upcoming Event: $eventName")
            .setContentText("Event time: $eventTime")
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}