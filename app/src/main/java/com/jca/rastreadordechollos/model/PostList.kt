package com.jca.rastreadordechollos.model

class PostList (
    var baseUrl : String,
    var posts : List<Post>
){
    fun append(morePosts : PostList) {
        this.posts = this.posts + morePosts.posts
    }
}