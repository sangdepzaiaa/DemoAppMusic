package com.example.musicapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat


class ApplicationClass: Application() {
     companion object{
         const val CHANNEL_ID = "CHANNEL_ID"
         const val PREVIOUS = "PREVIOUS"
         const val PLAY = "PLAY"
         const val NEXT = "NEXT"
         const val EXIT = "EXIT"
     }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notification = NotificationChannel(CHANNEL_ID,"music",NotificationManager.IMPORTANCE_HIGH)
            notification.description = "fff"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notification)
        }
    }

}