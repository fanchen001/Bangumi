package com.fanchen.imovie.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import com.fanchen.imovie.R
import com.fanchen.imovie.adapter.BangumiListAdapter
import com.fanchen.imovie.base.BaseAdapter
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.entity.face.IBangumiMoreRoot
import com.fanchen.imovie.entity.face.IVideo
import com.fanchen.imovie.retrofit.callback.RefreshCallback
import com.fanchen.imovie.retrofit.service.TucaoService
import com.fanchen.imovie.view.CustomEmptyView
import com.fanchen.imovie.view.dropdown.DropdownLayout
import kotlinx.android.synthetic.main.activity_search_list.*

/**
 * 吐槽C 视频搜索页面
 */
class SearchBangumiActivity : BaseToolbarActivity(), DropdownLayout.OnDropdownListListener, SwipeRefreshLayout.OnRefreshListener, BaseAdapter.OnLoadListener, BaseAdapter.OnItemClickListener {
    override val activityTitle: String
        get() = String.format(getString(R.string.search_mart), keyWord)

    private val TIDKEYS = arrayOf("全部", "新番", "动画", "音乐", "游戏", "三次元", "影视")
    private val TIDVALUES = arrayOf("", "24", "19", "20", "21", "22", "23")
    private val ORDERKEYS = arrayOf("发布日期", "弹幕数量", "播放数量")
    private val ORDERVALUES = arrayOf("date", "mukio", "views")

    private var page = 1
    private var keyWord: String? = null
    private var tid = TIDVALUES[0]
    private var order = ORDERVALUES[0]
    private var mVideoListAdapter: BangumiListAdapter? = null

    private val callback = object : RefreshCallback<IBangumiMoreRoot> {

        override fun onSuccess(enqueueKey: Int, response: IBangumiMoreRoot?) {
            if (response == null || !response.isSuccess || mVideoListAdapter == null) return
            if (page == 1) mVideoListAdapter!!.clear()
            val list = response.list
            if (list == null || list.size == 0) {
                showSnackbar(getString(R.string.not_more))
                mVideoListAdapter!!.setLoad(false)
                mVideoListAdapter!!.notifyDataSetChanged()
            } else {
                mVideoListAdapter!!.addAll(list)
                mVideoListAdapter!!.setLoad(list.size >= 10)
            }
        }

        override fun onStart(enqueueKey: Int) {
            if (cev_empty == null || swipe_refresh_layout == null || mVideoListAdapter == null) return
            cev_empty.setEmptyType(CustomEmptyView.TYPE_NON)
            if (!swipe_refresh_layout.isRefreshing && !mVideoListAdapter!!.isLoading) {
                swipe_refresh_layout.isRefreshing = true
            }
        }

        override fun onFailure(enqueueKey: Int, throwable: String) {
            if (cev_empty == null || mVideoListAdapter == null) return
            if (mVideoListAdapter!!.isEmpty)
                cev_empty.setEmptyType(CustomEmptyView.TYPE_ERROR)
            showSnackbar(throwable)
        }

        override fun onFinish(enqueueKey: Int) {
            if (cev_empty == null || swipe_refresh_layout == null || mVideoListAdapter == null) return
            swipe_refresh_layout.isRefreshing = false
            mVideoListAdapter!!.isLoading = false
            if (mVideoListAdapter!!.list.size == 0) {
                cev_empty.setEmptyType(CustomEmptyView.TYPE_EMPTY)
            }
        }

    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        keyWord = intent.getStringExtra(WORD)
        super.initActivity(savedState, inflater)
        dl_category.cols = 2
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        swipe_refresh_layout.setColorSchemeColors(typedValue.data)
        mVideoListAdapter = BangumiListAdapter(this, picasso)
        recycle_list.layoutManager = BaseAdapter.LinearLayoutManagerWrapper(this, BaseAdapter.LinearLayoutManagerWrapper.VERTICAL, false)
        recycle_list.adapter = mVideoListAdapter
        dl_category.setDropdownList(arrayOf(TIDKEYS, ORDERKEYS), arrayOf(TIDVALUES, ORDERVALUES))
        //加载数据
        loadNet(1)
    }

    override fun getLayout(): Int {
        return R.layout.activity_search_list
    }

    override fun setListener() {
        super.setListener()
        swipe_refresh_layout.setOnRefreshListener(this)
        dl_category.setOnDropdownListListener(this)
        mVideoListAdapter?.setOnLoadListener(this)
        mVideoListAdapter?.setOnItemClickListener(this)
    }

    override fun onItemClick(datas: List<*>, v: View, position: Int) {
        if (datas[position] !is IVideo) return
        val video = datas[position] as IVideo
        VideoDetailsActivity.startActivity(this, video)
    }

    override fun onRefresh() {
        loadNet(1)
    }

    override fun onLoad() {
        loadNet(++page)
    }

    /**
     * @param pager
     */
    private fun loadNet(pager: Int) {
        val integer = Integer.valueOf(pager)
        if (TextUtils.isEmpty(tid)) {
            //全部
            retrofitManager.enqueue(TucaoService::class.java, callback, "search", keyWord, integer, order)
        } else {
            //分区搜索
            retrofitManager.enqueue(TucaoService::class.java, callback, "search", keyWord, integer, tid, order)
        }
    }

    override fun OnDropdownListSelected(indexOfButton: Int, indexOfList: Int, textOfList: String, valueOfList: String) {
        when (indexOfButton) {
            0 -> tid = valueOfList
            1 -> order = valueOfList
        }
        loadNet(1)
    }

    override fun onDropdownListOpen() {}

    override fun onDropdownListClosed() {}

    companion object {

        const val WORD = "word"

        fun startActivity(activity: Activity, word: String) {
            try {
                val intent = Intent(activity, SearchBangumiActivity::class.java)
                intent.putExtra(WORD, word)
                activity.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
