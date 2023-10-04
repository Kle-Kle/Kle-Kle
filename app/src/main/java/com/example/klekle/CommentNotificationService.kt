package com.example.klekle

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo.PRIORITY_DEFAULT
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Objects

class CommentNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val CHANNEL_ID = "10000"
        val CHANNEL_NAME = "Klekle Channel"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("test:D", "Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.d("test:D", "Message notification body: ${it.title}")
            Log.d("test:D", "Message notification body: ${it.body}")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            val intentList = mutableListOf<Intent>()
            intentList.add(Intent(this, MainActivity::class.java))
            val pendingIntent = PendingIntent.getActivities(this, 11, intentList.toTypedArray(), PendingIntent.FLAG_IMMUTABLE)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(it.title)
                .setContentText(it.body)
                .setPriority(PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.icon_klekle)
                .setContentIntent(pendingIntent) // 알림 클릭 시, 클클 앱 오픈
                .setAutoCancel(true) // 알림 클릭 시, 알림 자동 삭제
                .build()

            notificationManager.notify(1, notification)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            // Get new FCM registration token
            Log.d("fcm:token", token)
        })
    }
}