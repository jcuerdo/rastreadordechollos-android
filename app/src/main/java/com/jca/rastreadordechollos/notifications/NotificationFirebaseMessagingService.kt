package com.jca.rastreadordechollos.notifications

import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val slag = message.data.get("slag")
        val bundle = Bundle()
        bundle.putString("slag", slag)

        val notificationBuilder = NotificationCompat.Builder(this, "POSTS")
            .setExtras(bundle)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        notificationManager.notify(1, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

}