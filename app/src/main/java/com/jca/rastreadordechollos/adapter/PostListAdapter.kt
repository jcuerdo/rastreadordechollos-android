package com.jca.rastreadordechollos.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.jca.rastreadordechollos.PostActivity
import com.jca.rastreadordechollos.R
import com.jca.rastreadordechollos.model.Post
import com.jca.rastreadordechollos.model.PostList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_post.view.postTitle
import kotlinx.android.synthetic.main.post_element.view.photo

class PostListAdapter(val dataSource: PostList) : BaseAdapter() {

    override fun getView(postition: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater = LayoutInflater.from(parent?.context);
        val postView = inflater.inflate(R.layout.post_element, parent, false)
        val post = getItem(postition) as Post;

        postView.postTitle.text = post.title

        fun showDetails(post : Post, view : View){
                val intent = Intent(
                    view.context,
                    PostActivity::class.java
                )
                intent.putExtra("title", post.title)
                intent.putExtra("link", post.link)
                intent.putExtra("baseUrl", dataSource.baseUrl)
                intent.putExtra("content", post.content)
                intent.putExtra("slag", post.slag)
            ContextCompat.startActivity(view.context, intent, null)
        }

        postView.photo.setOnClickListener { view -> showDetails(post,view) }
        postView.postTitle.setOnClickListener { view -> showDetails(post,view) }

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