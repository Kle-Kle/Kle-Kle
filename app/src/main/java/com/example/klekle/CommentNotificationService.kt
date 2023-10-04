package com.example.klekle

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo.PRIORITY_DEFAULT
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.util.UpdateFcmTokenRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject

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
        // TODO: 일단 해놓긴 했는데, 테스트 할 방법을 모르겠다..
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            // Get new FCM registration token
            val sharedPreferences = getSharedPreferences("login_info", Activity.MODE_PRIVATE)
            val userid = sharedPreferences.getString("loginedId", null).toString()
            updateFcmTokenThisUser(token, userid)
        })
    }

    private fun updateFcmTokenThisUser(token: String, userid: String) {
        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    if(success) {
                        Log.d("fcm:token", token)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val updateFcmTokenRequest = UpdateFcmTokenRequest(token, userid, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(updateFcmTokenRequest)
    }
}