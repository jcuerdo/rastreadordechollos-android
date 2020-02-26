package com.jca.rastreadordechollos

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.gson.GsonBuilder
import com.jca.rastreadordechollos.adapter.PostListAdapter
import com.jca.rastreadordechollos.model.PostList
import com.jca.rastreadordechollos.model.PostSingle
import com.jca.rastreadordechollos.view.PostListScrollView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.*
import java.io.IOException
import kotlin.random.Random
import kotlin.system.exitProcess


class MainActivity() : AppCompatActivity() {
    var loadingFooter: View? = null
    var page : Int  = 0;
    lateinit var baseUrl : String;
    lateinit var apiUrl : String;
    lateinit var apiPageUrl : String;
    lateinit var posts : PostList
    lateinit var adapter : PostListAdapter
    var fetching = false
    lateinit var mInterstitialAd: InterstitialAd
    var addOpened = false



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initVariables()
        initAds()

        val slag = intent.getStringExtra("slag")

        if( slag != null){
            loadPostActivityFromSlag(slag)
        }


        val inflater = LayoutInflater.from(applicationContext);
        this.loadingFooter = inflater.inflate(R.layout.footer_loading, null, false)
        postList.adapter = adapter


        fetchPosts()
        postList.setOnScrollListener(PostListScrollView({ fetchPosts() }))
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                addOpened = false
            }
        }
    }

    private fun initAds() {
        MobileAds.initialize(this) {}
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.adsense_interstitial)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun loadPostActivityFromSlag(slag: String?) {
        val url = baseUrl + slag
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                val gson = GsonBuilder().create()
                val post = gson.fromJson(response.body?.string(), PostSingle::class.java)
                val intent = Intent(applicationContext, PostActivity::class.java)
                intent.putExtra("title", post.post.title)
                intent.putExtra("link", post.post.link)
                intent.putExtra("baseUrl", baseUrl)
                intent.putExtra("content", post.post.content)
                intent.putExtra("slag", post.post.slag)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(applicationContext, intent, null)

            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
            }

        })
    }

    private fun initVariables() {
        baseUrl = getString(R.string.baseUrl)
        apiUrl = baseUrl + "api/"
        apiPageUrl = apiUrl + "?page=%d"
        posts = PostList(baseUrl, emptyList())
        adapter = PostListAdapter(posts)
    }


    fun fetchPosts() {

        if(this.fetching){
            return
        }

        if (mInterstitialAd.isLoaded && !addOpened) {
            if (Random.nextInt(1,5) == 4) {
                mInterstitialAd.show()
                addOpened = true
            }
        }

        this.fetching = true
        val url = String.format(this.apiPageUrl, this.page)
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        postList.addFooterView(this.loadingFooter)

        client.newCall(request).enqueue(object : Callback{
            override fun onResponse(call: Call, response: Response) {
                val gson = GsonBuilder().create()
                this@MainActivity.posts.append(gson.fromJson(response.body?.string(), PostList::class.java))
                this@MainActivity.page++

                runOnUiThread(){
                    adapter.notifyDataSetChanged()
                    this@MainActivity.postList.removeFooterView(this@MainActivity.loadingFooter)
                }

                this@MainActivity.fetching = false
            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
                runOnUiThread(){
                    Toast.makeText(this@MainActivity.applicationContext, getString(R.string.error_loading_posts), Toast.LENGTH_LONG).show()
                    this@MainActivity.postList.removeFooterView(this@MainActivity.loadingFooter)
                }

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_quit -> exitProcess(1)
            else -> super.onOptionsItemSelected(item)
        }
    }

}