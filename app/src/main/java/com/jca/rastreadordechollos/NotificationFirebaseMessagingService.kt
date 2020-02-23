package com.jca.rastreadordechollos

import android.content.Intent
import android.widget.Toast
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
        if(slag != null) {
            val url = this.apiUrl + slag
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val gson = GsonBuilder().create()
                    val post = gson.fromJson(response?.body?.string(), PostSingle::class.java)
                    val intent = Intent(applicationContext, PostActivity::class.java)
                    intent.putExtra("title", post.post.title)
                    intent.putExtra("link", post.post.link)
                    intent.putExtra("baseUrl", baseUrl)
                    intent.putExtra("content", post.post.content)
                    intent.putExtra("slag", post.post.slag)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    ContextCompat.startActivity(applicationContext, intent, null)

                }

                override fun onFailure(call: Call, e: IOException) {
                    println(e.message)
                }

            })

        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println(token)
    }

}