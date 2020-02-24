package com.jca.rastreadordechollos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.*
import java.io.IOException

class NotificationFirebaseMessagingService : FirebaseMessagingService() {

    var baseUrl : String = "https://www.rastreadordechollos.com/";
    var apiUrl : String = baseUrl + "api/";

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
        println(token)
    }

}