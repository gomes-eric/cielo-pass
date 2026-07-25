package com.cielo.cielopass.core.cielo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.cielo.cielopass.core.constants.NotificationConstants.ACTION_START_PAYMENT_SERVICE
import com.cielo.cielopass.core.constants.NotificationConstants.ACTION_STOP_PAYMENT_SERVICE
import com.cielo.cielopass.core.constants.NotificationConstants.CHANNEL_DESCRIPTION
import com.cielo.cielopass.core.constants.NotificationConstants.CHANNEL_ID
import com.cielo.cielopass.core.constants.NotificationConstants.CHANNEL_NAME
import com.cielo.cielopass.core.constants.NotificationConstants.NOTIFICATION_ID
import com.cielo.cielopass.core.constants.NotificationConstants.NOTIFICATION_TEXT
import com.cielo.cielopass.core.constants.NotificationConstants.NOTIFICATION_TITLE

class CieloForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val action = intent?.action
        if (action == ACTION_STOP_PAYMENT_SERVICE) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()

            return START_NOT_STICKY
        }

        createNotificationChannel()
        val notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_TEXT)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                IMPORTANCE_LOW,
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        fun start(context: Context) {
            try {
                val intent = Intent(context, CieloForegroundService::class.java).apply {
                    action = ACTION_START_PAYMENT_SERVICE
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {
                // Safely handle environments where Intent or Service cannot be launched (e.g. unit tests)
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, CieloForegroundService::class.java).apply {
                    action = ACTION_STOP_PAYMENT_SERVICE
                }

                context.startService(intent)
            } catch (_: Exception) {
                // Safely handle environments where Intent or Service cannot be launched (e.g. unit tests)
            }
        }
    }
}
