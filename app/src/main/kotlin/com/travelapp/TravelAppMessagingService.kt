package com.travelapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.travelapp.debugmenu.DebugMenu
import timber.log.Timber
import kotlin.random.Random

class TravelAppMessagingService : FirebaseMessagingService() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "TravelApp"
        private const val LINK_KEY = "link"
    }

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.w("New token received: $token")
        DebugMenu.setDebugValue(applicationContext, DebugMenu.PUSH_TOKEN_KEY, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val intent = Intent(Intent.ACTION_VIEW, message.data[LINK_KEY]?.toUri(), this, StartActivity::class.java)
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TASK)

        val id = Random.nextInt()

        val pendingIntent = PendingIntent.getActivity(
            this,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val notificationBuilder = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_16_push_notification)
            .setAutoCancel(true)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(id, notificationBuilder.build())
    }
}