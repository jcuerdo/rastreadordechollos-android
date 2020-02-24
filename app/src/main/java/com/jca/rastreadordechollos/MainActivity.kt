package com.jca.rastreadordechollos

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.post_element.view.*
import okhttp3.*
import java.io.IOException
import kotlin.random.Random
import kotlin.system.exitProcess


class MainActivity() : AppCompatActivity() {
    var loadingFooter: View? = null
    var page : Int  = 0;
    var baseUrl : String = "https://www.rastreadordechollos.com/";
    var apiUrl : String = baseUrl + "api/?page=%d";
    var posts = PostList(baseUrl, emptyList())
    var adapter = PostListAdapter(this.posts)
    var fetching = false
    lateinit var mInterstitialAd: InterstitialAd
    var addOpened = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        MobileAds.initialize(this) {}
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-6904186947817626/3814932797"

        mInterstitialAd.loadAd(AdRequest.Builder().build())


        val inflater = LayoutInflater.from(applicationContext);
        this.loadingFooter = inflater.inflate(R.layout.footer_loading, null, false)

        postList.adapter = adapter


        fetchPosts()
        postList.setOnScrollListener(PostListScrollView({fetchPosts()}))
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                addOpened = false
            }
        }
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
        val url = String.format(this.apiUrl, this.page)
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()


        postList.addFooterView(this.loadingFooter)
        postList.setSelection(10000)


        client.newCall(request).enqueue(object : Callback{
            override fun onResponse(call: Call, response: Response) {
                val gson = GsonBuilder().create()
                this@MainActivity.posts.append(gson.fromJson(response?.body?.string(), PostList::class.java))
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
                    Toast.makeText(this@MainActivity.applicationContext, "ERRORACO", Toast.LENGTH_LONG).show()
                    this@MainActivity.postList.removeFooterView(this@MainActivity.loadingFooter)
                }

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_quit -> exitProcess(1)
            else -> super.onOptionsItemSelected(item)
        }
    }

}

class Post (
    val title : String,
    val slag : String,
    val date : String,
    val link : String,
    val content: String,
    val image : String
){

}

class PostList (
    var baseUrl : String,
    var posts : List<Post>
){
    fun append(morePosts :  PostList) {
        this.posts = this.posts + morePosts.posts
    }
}

class PostSingle (
    var post : Post
){
}

class PostListScrollView(var fetchPosts : () -> Unit) : AbsListView.OnScrollListener{

    private var scrollState: Int = -1

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (
            view?.lastVisiblePosition == totalItemCount - 1 )
        {
            fetchPosts.invoke()
        }

    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
        this.scrollState = scrollState
    }
}

class PostListAdapter(val dataSource: PostList) : BaseAdapter() {

    override fun getView(postition: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater = LayoutInflater.from(parent?.context);
        val postView = inflater.inflate(R.layout.post_element, parent, false)
        val post = getItem(postition) as Post;

        postView.postTitle.text = post.title

        fun showDetails(post : Post, view : View){
                val intent = Intent(view.context, PostActivity::class.java)
                intent.putExtra("title", post.title)
                intent.putExtra("link", post.link)
                intent.putExtra("baseUrl", dataSource.baseUrl)
                intent.putExtra("content", post.content)
                intent.putExtra("slag", post.slag)
                startActivity(view.context, intent, null)
        }

        postView.photo.setOnClickListener { view -> showDetails(post,view) }
        postView.postTitle.setOnClickListener { view -> showDetails(post,view) }

        /**
        postView.showButton.setOnClickListener { view -> showDetails(post,view) }

        postView.shareButton.setOnClickListener { view ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.type= "text/plain"
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, post.title)
            sendIntent.putExtra(Intent.EXTRA_TEXT,  dataSource.baseUrl + post.slag)

            var shareIntent = Intent.createChooser(sendIntent, null)

            startActivity(view.context, shareIntent,null)
        }
**/

        val picasso = Picasso.Builder(parent?.context)
            .listener {
                    _, _, e ->
                println(e.message)
            }

            .build()

        picasso.load(post.image).placeholder(R.drawable.ic_action_name).into(postView.photo)


        return postView
    }

    override fun getItem(position: Int): Any {
        return dataSource.posts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.posts.size
    }

}