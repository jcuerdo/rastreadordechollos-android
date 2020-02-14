package com.jca.rastreadordechollos

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.content_post.*

class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        setSupportActionBar(toolbar)

        val htmlDocument = intent.getStringExtra("content")

        webView.loadDataWithBaseURL(null, htmlDocument,
            "text/HTML", "UTF-8", null)
    }

}
