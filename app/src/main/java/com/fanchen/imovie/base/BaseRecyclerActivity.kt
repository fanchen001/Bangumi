package com.fanchen.imovie.base

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanchen.imovie.R
import com.fanchen.imovie.retrofit.RetrofitManager
import com.fanchen.imovie.retrofit.callback.RefreshCallback
import com.fanchen.imovie.thread.task.AsyTaskListener
import com.fanchen.imovie.view.CustomEmptyView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_recyclerview.*

/**
 * layout为R.layout.activity_recyclerview
 * 以RecyclerView为显示根布局的activity继承该类
 */
abstract class BaseRecyclerActivity : BaseToolbarActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, BaseAdapter.OnItemClickListener, BaseAdapter.OnLoadListener {

    var page = 1
    private var pageStart = 1
    private var mAdapter: BaseAdapter? = null

    /**
     * @return
     */
    protected val isRefresh: Boolean
        get() = page <= pageStart

    /**
     * @return
     */
    protected abstract val layoutManager: RecyclerView.LayoutManager

    lateinit var mTextView: TextView
    var mRecyclerView: RecyclerView? = null
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    lateinit var mCustomEmptyView: CustomEmptyView

    /**
     *
     */
    protected var scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val picasso = picasso
            if (picasso != null) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    picasso.resumeTag(BaseRecyclerActivity::class.java)
                } else {
                    picasso.pauseTag(BaseRecyclerActivity::class.java)
                }
            }
        }

    }

    override fun getLayout(): Int {
        return R.layout.activity_recyclerview
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        if (!checkFieldNull()) {
            swipe_refresh_layout!!.setColorSchemeColors(typedValue.data)
            recycle_list.layoutManager = layoutManager
            recycle_list.adapter = getAdapter(picasso)
        }
        loadData(retrofitManager, page = pageStart)
    }

    override fun setListener() {
        super.setListener()
        if (!checkFieldNull()) {
            swipe_refresh_layout!!.setOnRefreshListener(this)
            cev_empty!!.setOnClickListener(this)
            recycle_list.addOnScrollListener(scrollListener)
        }
        if (mAdapter != null) {
            mAdapter!!.setOnItemClickListener(this)
            if (hasLoad()) mAdapter!!.setOnLoadListener(this)
        }
    }

    private fun checkFieldNull(): Boolean {
        return swipe_refresh_layout == null || cev_empty == null || recycle_list == null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_top_back -> finish()
            R.id.cev_empty -> onRefresh()
        }
    }

    override fun onRefresh() {
        loadData(retrofitManager, page = pageStart)
    }

    override fun onLoad() {
        loadData(retrofitManager, ++page)
    }

    fun setPageStart(page: Int) {
        this.pageStart = page
    }

    /**
     * @return
     */
    protected open fun hasLoad(): Boolean {
        return false
    }

    /**
     * @return
     */
    protected abstract fun getAdapter(picasso: Picasso?): BaseAdapter

    /**
     * @param page
     */
    protected abstract fun loadData(retrofit: RetrofitManager, page: Int)

    /**
     * @param <T>
    </T> */
    protected abstract inner class RefreshRecyclerActivityImpl<T> : RefreshCallback<T> {

        override fun onStart(enqueueKey: Int) {
            if (cev_empty == null || swipe_refresh_layout == null || mAdapter == null) return
            cev_empty!!.setEmptyType(CustomEmptyView.TYPE_NON)
            if (!swipe_refresh_layout!!.isRefreshing && !mAdapter!!.isLoading) {
                swipe_refresh_layout!!.isRefreshing = true
            }
        }

        override fun onFailure(enqueueKey: Int, throwable: String) {
            if (cev_empty == null || mAdapter == null) return
            if (mAdapter!!.isEmpty) cev_empty!!.setEmptyType(CustomEmptyView.TYPE_ERROR)
            showSnackbar(throwable)
        }

        override fun onFinish(enqueueKey: Int) {
            if (cev_empty == null || swipe_refresh_layout == null || mAdapter == null) return
            swipe_refresh_layout!!.isRefreshing = false
            mAdapter!!.isLoading = false
            if (mAdapter!!.isEmpty) {
                cev_empty!!.setEmptyType(CustomEmptyView.TYPE_EMPTY)
            }
        }
    }

    protected abstract inner class TaskRecyclerActivityImpl<T> : AsyTaskListener<T> {

        override fun onTaskFinish() {
            if (cev_empty == null || swipe_refresh_layout == null || mAdapter == null) return
            swipe_refresh_layout!!.isRefreshing = false
            mAdapter!!.isLoading = false
            if (mAdapter!!.isEmpty) {
                cev_empty!!.setEmptyType(CustomEmptyView.TYPE_EMPTY)
            }
        }

        override fun onTaskStart() {
            if (cev_empty == null || swipe_refresh_layout == null) return
            cev_empty!!.setEmptyType(CustomEmptyView.TYPE_NON)
            if (!swipe_refresh_layout!!.isRefreshing && !mAdapter!!.isLoading) {
                swipe_refresh_layout!!.isRefreshing = true
            }
        }
    }
}
