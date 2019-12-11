package com.appsys.android.popularmovie.view

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Created by shakir on 8/7/2017.
 */
abstract class EndlessRecyclerViewScrollListener(private val layoutManager: RecyclerView.LayoutManager) : RecyclerView.OnScrollListener() {

    private var visibleThreshold = 5
    private var currentPage = 1
    private var previousTotalItemCount = 0
    private var loading = true
    private val startingPageIndex = 1

    constructor(layoutManager: LinearLayoutManager) : this(layoutManager as RecyclerView.LayoutManager)

    constructor(layoutManager: GridLayoutManager) : this(layoutManager as RecyclerView.LayoutManager) {
        visibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager) : this(layoutManager as RecyclerView.LayoutManager) {
        visibleThreshold *= layoutManager.spanCount
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0)
                maxSize = lastVisibleItemPositions[i]
            else if (lastVisibleItemPositions[i] > maxSize)
                maxSize = lastVisibleItemPositions[i]
        }
        return maxSize
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = when (layoutManager) {
            is StaggeredGridLayoutManager -> getLastVisibleItem(layoutManager.findLastVisibleItemPositions(null))
            is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            else -> 0
        }
        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0)
                loading = true
        }

        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, view)
            loading = true
        }
    }


    fun resetState() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
}