package com.example.navhost1.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.navhost1.R

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        val manager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channelId = "recordatorios"

        val channel = NotificationChannel(
            channelId,
            "Recordatorios",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        manager.createNotificationChannel(channel)

        val notification =
            NotificationCompat.Builder(
                applicationContext,
                channelId
            )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("NeuraBloom")
                .setContentText(
                    "Escribe en tu diario emocional hoy."
                )
                .setAutoCancel(true)
                .build()

        manager.notify(1, notification)

        return Result.success()
    }
}