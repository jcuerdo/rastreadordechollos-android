package com.jca.rastreadordechollos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.content_post.*


class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        setSupportActionBar(toolbar)

        val htmlDocument = intent.getStringExtra("content")
        val title = intent.getStringExtra("title")
        val slag = intent.getStringExtra("slag")
        val link = intent.getStringExtra("link")
        val baseUrl = intent.getStringExtra("baseUrl")

        webView.loadDataWithBaseURL(null, htmlDocument,
            "text/HTML", "UTF-8", null)

        postTitle.text = title

        goOriginalButton.setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            startActivity(intent)
        }

        shareButton.setOnClickListener { view ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.type= "text/plain"
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
            sendIntent.putExtra(Intent.EXTRA_TEXT,  baseUrl + slag)

            var shareIntent = Intent.createChooser(sendIntent, null)

            ContextCompat.startActivity(view.context, shareIntent, null)
        }


    }

}
