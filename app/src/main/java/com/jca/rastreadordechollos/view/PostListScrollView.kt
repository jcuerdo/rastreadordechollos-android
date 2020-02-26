package com.jca.rastreadordechollos.view

import android.widget.AbsListView

class PostListScrollView(var fetchPosts : () -> Unit) : AbsListView.OnScrollListener {

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