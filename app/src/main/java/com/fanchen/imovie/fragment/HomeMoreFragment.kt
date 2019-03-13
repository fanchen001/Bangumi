package com.fanchen.imovie.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.fanchen.imovie.R
import com.fanchen.imovie.activity.*
import com.fanchen.imovie.base.BaseFragment
import com.fanchen.imovie.entity.JsonSerialize
import com.fanchen.imovie.entity.bili.BilibiliIndex
import com.fanchen.imovie.entity.face.ISearchWord
import com.fanchen.imovie.entity.face.IViewType
import com.fanchen.imovie.retrofit.RetrofitManager
import com.fanchen.imovie.retrofit.callback.RefreshCallback
import com.fanchen.imovie.retrofit.service.BilibiliService
import com.fanchen.imovie.thread.AsyTaskQueue
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl
import com.fanchen.imovie.util.DisplayUtil
import com.fanchen.imovie.util.LogUtil
import com.fanchen.imovie.view.FlowLayout
import com.google.gson.Gson
import com.litesuits.orm.db.assit.QueryBuilder
import com.litesuits.orm.db.assit.WhereBuilder
import kotlinx.android.synthetic.main.fragment_more.*
import java.util.*

/**
 * 更多
 */
class HomeMoreFragment : BaseFragment(), View.OnClickListener, FlowLayout.OnFlowItemClick, SwipeRefreshLayout.OnRefreshListener {

    protected var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    private var mSaveWordIndex: BilibiliIndex? = null
    private var serializeKey: String? = null

    private val callback = object : RefreshCallback<BilibiliIndex> {

        override fun onStart(enqueueKey: Int) {
            if (mSwipeRefreshLayout == null || tv_word_error == null) return
            tv_word_error!!.visibility = View.GONE
            mSwipeRefreshLayout!!.isRefreshing = true
        }

        override fun onFinish(enqueueKey: Int) {
            if (mSwipeRefreshLayout == null) return
            mSwipeRefreshLayout!!.isRefreshing = false
        }

        override fun onFailure(enqueueKey: Int, throwable: String) {
            if (tv_word_error == null) return
            tv_word_error!!.visibility = View.VISIBLE
            showSnackbar(throwable)
        }

        override fun onSuccess(enqueueKey: Int, response: BilibiliIndex?) {
            if (flowlayout_work == null || response == null || isDetached) return
            mSaveWordIndex = response
            flowlayout_work!!.removeAllViews()
            flowlayout_work!!.addDataList2TextView(getListData(response))
            AsyTaskQueue.newInstance().execute(SaveTaskListener(response))
        }

    }

    override fun getLayout(): Int {
        return R.layout.fragment_more
    }

    override fun findView(v: View) {
        super.findView(v)
        mSwipeRefreshLayout = v as SwipeRefreshLayout
    }

    override fun initFragment(savedInstanceState: Bundle?, args: Bundle?) {
        super.initFragment(savedInstanceState, args)
        serializeKey = javaClass.simpleName
        val typedValue = TypedValue()
        activity.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        mSwipeRefreshLayout!!.setColorSchemeColors(typedValue.data)
        mSaveWordIndex = savedInstanceState?.getParcelable(INDEX)
        if (savedInstanceState != null && mSaveWordIndex != null) {
            if (mSaveWordIndex!!.data != null && mSaveWordIndex!!.data.list != null) {
                flowlayout_work!!.removeAllViews()
                flowlayout_work!!.addDataList2TextView(getListData(mSaveWordIndex))
            }
        } else {
            AsyTaskQueue.newInstance().execute(QueryTaskListener(retrofitManager))
        }
    }

    override fun setListener() {
        super.setListener()
        ll_hack!!.setOnClickListener(this)
        ll_more_game!!.setOnClickListener(this)
        ll_more_apk!!.setOnClickListener(this)
        search_bar!!.setOnClickListener(this)
        ll_more_hotword!!.setOnClickListener(this)
        qr_scan!!.setOnClickListener(this)
        flowlayout_work!!.setOnFlowItemClick(this)
        ll_game_pc!!.setOnClickListener(this)
        ll_acg_tree!!.setOnClickListener(this)
        tv_word_error!!.setOnClickListener(this)
        mSwipeRefreshLayout!!.setOnRefreshListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_word_error -> onRefresh()
            R.id.ll_more_apk -> ApkListActivity.startActivity(activity, ApkListActivity.TYPE_APK)
            R.id.ll_more_game -> ApkListActivity.startActivity(activity, ApkListActivity.TYPE_GAME)
            R.id.ll_acg_tree -> AcgTabActivity.startActivity(activity)
            R.id.ll_game_pc -> ApkEvaluatActivity.startActivity(activity)
            R.id.search_bar -> {
                val parentFragment = parentFragment
                if (parentFragment != null && parentFragment is HomePagerFragment) {
                    parentFragment.openSearchDialog()
                }
            }
            R.id.ll_hack -> HackerToolActivity.startActivity(activity)
            R.id.qr_scan -> CaptureActivity.startActivity(activity)
            R.id.ll_more_hotword -> {
                val layoutParams = nsv_hotword!!.layoutParams
                if (layoutParams.height == DisplayUtil.dip2px(activity, DIP_192.toFloat())) {
                    layoutParams.height = DisplayUtil.dip2px(activity, DIP_96.toFloat())
                    (ll_more_hotword!!.getChildAt(1) as TextView).text = "  查看更多"
                    val drawable = resources.getDrawable(R.drawable.ic_arrow_down_gray_round)
                    (ll_more_hotword!!.getChildAt(1) as TextView).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                } else {
                    layoutParams.height = DisplayUtil.dip2px(activity, DIP_192.toFloat())
                    val drawable = resources.getDrawable(R.drawable.ic_arrow_up_gray_round)
                    (ll_more_hotword!!.getChildAt(1) as TextView).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    (ll_more_hotword!!.getChildAt(1) as TextView).text = "  收起"
                }
                nsv_hotword!!.layoutParams = layoutParams
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (mSaveWordIndex != null) {
            outState.putParcelable(INDEX, mSaveWordIndex)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRefresh() {
        loadDate(retrofitManager!!)
    }

    override fun <T> OnItemClick(v: View, data: T, position: Int) {
        val parentFragment = parentFragment
        if (parentFragment != null && parentFragment is HomePagerFragment) {
            parentFragment.onSearchClick(object : ISearchWord {

                override fun getViewType(): Int {
                    return IViewType.TYPE_NORMAL
                }

                override fun getType(): Int {
                    return ISearchWord.TYPE_WORD
                }

                override fun getWord(): String {
                    return data.toString()
                }

            })
        }
    }

    fun getListData(result: BilibiliIndex?): List<String> {
        val all = ArrayList<String>()
        if (result == null || result.data == null || result.data.list == null)
            return all
        for (s in result.data.list) {
            all.add(s.keyword)
        }
        return all
    }

    /**
     *
     */
    private fun loadDate(retrofitManager: RetrofitManager) {
        retrofitManager.enqueue(BilibiliService::class.java, callback, "loadHotword", System.currentTimeMillis().toString())
    }

    private inner class QueryTaskListener(private val retrofit: RetrofitManager) : AsyTaskListenerImpl<BilibiliIndex>() {

        override fun onTaskStart() {
            if (mSwipeRefreshLayout == null) return
            if (!mSwipeRefreshLayout!!.isRefreshing) {
                mSwipeRefreshLayout!!.isRefreshing = true
            }
        }

        override fun onTaskBackground(): BilibiliIndex? {
            if (liteOrm == null) return null
            val query = liteOrm!!.query(QueryBuilder(JsonSerialize::class.java).where("key = ?", serializeKey))
            if (query != null && query.size > 0) {
                val jsonSerialize = query[0]
                if (!jsonSerialize.isStale) {
                    //数据未过期
                    return Gson().fromJson(jsonSerialize.json, BilibiliIndex::class.java)
                }
            }
            return null
        }

        override fun onTaskSuccess(data: BilibiliIndex?) {
            if (flowlayout_work == null || mSwipeRefreshLayout == null) return
            mSaveWordIndex = data
            if (data != null) {
                //加载本地数据
                LogUtil.d(HomeMoreFragment::class.java, "加载本地数据")
                flowlayout_work!!.addDataList2TextView(getListData(data))
                mSwipeRefreshLayout!!.isRefreshing = false
            } else {
                //没有缓存数据或者数据已经过期，加载网络数据
                LogUtil.d(HomeMoreFragment::class.java, "加载网络数据")
                loadDate(retrofit)
            }
        }


    }

    private inner class SaveTaskListener(private val response: BilibiliIndex?) : AsyTaskListenerImpl<Void>() {

        override fun onTaskBackground(): Void? {
            if (liteOrm == null || response == null) return null
            //保存key
            liteOrm!!.delete(WhereBuilder(JsonSerialize::class.java, "key = ?", arrayOf<Any>(serializeKey!!)))
            liteOrm!!.insert(JsonSerialize(response, serializeKey))
            return null
        }

    }

    companion object {
        val DIP_96 = 96
        val DIP_192 = 192
        val INDEX = "index"

        fun newInstance(): HomeMoreFragment {
            return HomeMoreFragment()
        }
    }

}
