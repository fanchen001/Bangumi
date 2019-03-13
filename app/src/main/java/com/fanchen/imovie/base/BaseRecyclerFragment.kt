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
import com.fanchen.imovie.entity.JsonSerialize
import com.fanchen.imovie.retrofit.RetrofitManager
import com.fanchen.imovie.retrofit.callback.RefreshCallback
import com.fanchen.imovie.thread.AsyTaskQueue
import com.fanchen.imovie.thread.task.AsyTaskListener
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl
import com.fanchen.imovie.util.LogUtil
import com.fanchen.imovie.util.NetworkUtil
import com.fanchen.imovie.view.CustomEmptyView
import com.google.gson.Gson
import com.litesuits.orm.db.assit.QueryBuilder
import com.litesuits.orm.db.assit.WhereBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import kotlinx.android.synthetic.main.fragment_recyclerview.view.*
import java.lang.reflect.Type

abstract class BaseRecyclerFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, BaseAdapter.OnItemClickListener, BaseAdapter.OnLoadListener {
    /**
     * 当前页数
     *
     * @return
     */
    var page = 1
    private var pageStart = 1
    private var mAdapter: BaseAdapter? = null
    private var savedInstanceState: Bundle? = null

    /**
     * RecyclerView  LayoutManager
     *
     * @return
     */
    abstract val layoutManager: RecyclerView.LayoutManager

    /**
     * @return
     */
    val isRefresh: Boolean
        get() = page == pageStart

    /**
     * @return
     */
    open val serializeClass: Type?
        get() = null

    open val staleTime: Int
        get() = 12

    /**
     * @return
     */
    open val serializeKey: String
        get() = javaClass.simpleName

    lateinit var mTextView: TextView
    var mRecyclerView: RecyclerView? = null
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    lateinit var mCustomEmptyView: CustomEmptyView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        mTextView = view!!.tv_recycler_bottom
        mRecyclerView = view.recycle
        mSwipeRefreshLayout = view.swipe_refresh_layout
        mCustomEmptyView = view.empty_layout
        return view
    }

    fun getSwipeRefreshLayout(): SwipeRefreshLayout? {
        return mSwipeRefreshLayout
    }

    fun getRecyclerView(): RecyclerView? {
        return mRecyclerView
    }

    /**
     *
     */
    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recycle: RecyclerView?, newState: Int) {
            val picasso = picasso
            if (picasso != null) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    picasso.resumeTag(BaseRecyclerFragment::class.java)
                } else {
                    picasso.pauseTag(BaseRecyclerFragment::class.java)
                }
            }
        }

    }

    override fun getLayout(): Int {
        return R.layout.fragment_recyclerview
    }

    override fun initFragment(savedInstanceState: Bundle?, args: Bundle?) {
        super.initFragment(savedInstanceState, args)
        this.savedInstanceState = savedInstanceState
        setHasOptionsMenu(true)
        val typedValue = TypedValue()
        if (activity != null) activity.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        if (!checkFieldNull()) {
            swipe_refresh_layout!!.setColorSchemeColors(typedValue.data)
            swipe_refresh_layout!!.isEnabled = hasRefresh()
            recycle.layoutManager = layoutManager
            recycle.adapter = getAdapter(picasso)
        }
        if (useLocalStorage() && savedInstanceState == null) {
            loadLocalData(AsyTaskQueue.newInstance())
        } else {
            LogUtil.e(BaseRecyclerFragment::class.java, "加载网络数据")
            loadData(savedInstanceState, retrofitManager, page = pageStart)
        }
    }

    override fun setListener() {
        super.setListener()
        if (!checkFieldNull()) {
            if (hasRefresh())
                swipe_refresh_layout!!.setOnRefreshListener(this)
            empty_layout!!.setOnClickListener(this)
            recycle!!.addOnScrollListener(scrollListener)
        }
        if (mAdapter != null) {
            if (hasLoad())
                mAdapter!!.setOnLoadListener(this)
            mAdapter!!.setOnItemClickListener(this)
        }
    }

    private fun checkFieldNull(): Boolean {
        return swipe_refresh_layout == null || empty_layout == null || recycle == null
    }

    /**
     * 能否刷新
     *
     * @return
     */
    protected open fun hasRefresh(): Boolean {
        return true
    }

    /**
     * RecyclerView Adapter
     *
     * @return
     */
    abstract fun getAdapter(picasso: Picasso?): BaseAdapter

    /**
     * 加载网络数据
     *
     * @param page
     */
    abstract fun loadData(savedInstanceState: Bundle?, retrofit: RetrofitManager?, page: Int)

    /**
     * 加载本地缓存的数据
     */
    open fun loadLocalData(queue: AsyTaskQueue) {

    }

    /**
     * 能否加载更多
     *
     * @return
     */
    protected open fun hasLoad(): Boolean {
        return true
    }

    fun setPageStart(pageStart: Int) {
        this.pageStart = pageStart
    }

    override fun onRefresh() {
        loadData(null, retrofitManager, page = pageStart)
    }

    override fun onLoad() {
        loadData(null, retrofitManager, ++page)
    }

    /**
     * 是否使用本地存储
     *
     * @return
     */
    protected open fun useLocalStorage(): Boolean {
        return false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.empty_layout -> onRefresh()
        }
    }


    /**
     *
     */
    private inner class SaveTaskListener(private val response: Any) : AsyTaskListenerImpl<Void>() {

        override fun onTaskBackground(): Void? {
            if (liteOrm == null) return null
            //保存key
            liteOrm!!.delete(WhereBuilder(JsonSerialize::class.java, "key = ?", arrayOf<Any>(serializeKey)))
            liteOrm!!.insert(JsonSerialize(response, serializeKey))
            return null
        }

    }

    /**
     * @param <T>
    </T> */
    abstract inner class QueryTaskListener<T> : AsyTaskListenerImpl<T>() {

        override fun onTaskStart() {
            if (empty_layout == null || swipe_refresh_layout == null || mAdapter == null) return
            empty_layout!!.setEmptyType(CustomEmptyView.TYPE_NON)
            if (!swipe_refresh_layout!!.isRefreshing && !mAdapter!!.isLoading) {
                swipe_refresh_layout!!.isRefreshing = true
            }
        }

        override fun onTaskBackground(): T? {
            if (liteOrm == null) return null
            val query = liteOrm!!.query(QueryBuilder(JsonSerialize::class.java).where("key = ?", serializeKey))
            if (query != null && query.size > 0) {
                val jsonSerialize = query[0]
                //数据未过期 或者当前无网络情况下，返回缓存的数据
                if (!jsonSerialize.isStale(staleTime) || !NetworkUtil.isNetWorkAvailable(activity)) {
                    try {
                        val serializeClass = serializeClass
                        if (serializeClass != null) {
                            return Gson().fromJson<T>(jsonSerialize.json, serializeClass)
                        } else if (jsonSerialize.isRawType) {
                            val forName = Class.forName(jsonSerialize.clazz)
                            return Gson().fromJson<T>(jsonSerialize.json, forName)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
            return null
        }

        override fun onTaskSuccess(data: T?) {
            if (swipe_refresh_layout == null) return
            if (data != null) {
                onSuccess(data)
                swipe_refresh_layout!!.isRefreshing = false
                LogUtil.e(BaseRecyclerFragment::class.java, "加载本地数据")
            } else {
                //加载网络数据
                LogUtil.e(BaseRecyclerFragment::class.java, "加载网络数据")
                loadData(savedInstanceState, retrofitManager, page = pageStart)
            }
        }

        /**
         * @param date
         */
        abstract fun onSuccess(date: T)
    }


    /**
     * @param <T>
    </T> */
    protected abstract inner class RefreshRecyclerFragmentImpl<T> : RefreshCallback<T> {

        override fun onStart(enqueueKey: Int) {
            if (empty_layout == null || swipe_refresh_layout == null) return
            empty_layout!!.setEmptyType(CustomEmptyView.TYPE_NON)
            if (!swipe_refresh_layout!!.isRefreshing && !mAdapter!!.isLoading) {
                swipe_refresh_layout!!.isRefreshing = true
            }
        }

        override fun onFailure(enqueueKey: Int, throwable: String) {
            if (empty_layout == null || mAdapter == null) return
            if (mAdapter!!.isEmpty) empty_layout!!.setEmptyType(CustomEmptyView.TYPE_ERROR)
            showSnackbar(throwable)
        }

        override fun onFinish(enqueueKey: Int) {
            if (empty_layout == null || mAdapter == null || mAdapter == null) return
            swipe_refresh_layout!!.isRefreshing = false
            mAdapter!!.isLoading = false
            if (mAdapter!!.isEmpty) {
                empty_layout!!.setEmptyType(CustomEmptyView.TYPE_EMPTY)
            }
        }

        override fun onSuccess(enqueueKey: Int, response: T?) {
            if (response == null || mAdapter == null) return
            if (isRefresh) mAdapter!!.clear()
            if (useLocalStorage()) {
                //将数据序列化到本地
                LogUtil.e(BaseRecyclerFragment::class.java, "序列化数据到本地")
                AsyTaskQueue.newInstance().execute(SaveTaskListener(response))
            }
            onSuccess(response)
        }

        /**
         * @param response
         */
        abstract fun onSuccess(response: T)
    }

    protected abstract inner class TaskRecyclerFragmentImpl<T> : AsyTaskListener<T> {

        override fun onTaskFinish() {
            if (isDetached || !isAdded) return
            swipe_refresh_layout!!.isRefreshing = false
            mAdapter!!.isLoading = false
            if (mAdapter!!.isEmpty) {
                empty_layout!!.setEmptyType(CustomEmptyView.TYPE_EMPTY)
            }
        }

        override fun onTaskStart() {
            if (isDetached || !isAdded) return
            empty_layout!!.setEmptyType(CustomEmptyView.TYPE_NON)
            if (swipe_refresh_layout != null && !swipe_refresh_layout.isRefreshing && !mAdapter!!.isLoading) {
                swipe_refresh_layout.isRefreshing = true
            }
        }
    }

}
