package com.fanchen.imovie.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.fanchen.imovie.R
import com.fanchen.imovie.adapter.ShortVideoAdapter
import com.fanchen.imovie.adapter.pager.ShortVideoPagerAdapter
import com.fanchen.imovie.base.BaseAdapter
import com.fanchen.imovie.base.BaseRecyclerFragment
import com.fanchen.imovie.base.BaseTabActivity
import com.fanchen.imovie.entity.dytt.DyttShortVideo
import com.fanchen.imovie.view.video.IjkVideoView
import com.fanchen.imovie.view.video.SuperPlayerManage
import com.fanchen.imovie.view.video.SuperPlayerView
import kotlinx.android.synthetic.main.activity_tab_pager.*

/**
 * 短视频
 */
class ShortVideoTabActivity : BaseTabActivity(), ShortVideoAdapter.OnItemPlayClick, Runnable, RecyclerView.OnChildAttachStateChangeListener {

    lateinit var superPlayerView: SuperPlayerView

    var position = -1
    var lastPosition = -1


    private val fragmentRecyclerView: RecyclerView?
        get() {
            val visibleFragment = visibleFragment as BaseRecyclerFragment
            return visibleFragment.mRecyclerView
        }

    private val fragmentLinearLayoutManager: BaseAdapter.LinearLayoutManagerWrapper?
        get() {
            val visibleFragment = visibleFragment as BaseRecyclerFragment
            return visibleFragment.layoutManager as BaseAdapter.LinearLayoutManagerWrapper
        }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        superPlayerView = SuperPlayerManage.getSuperManage().initialize(this)
        superPlayerView.setShowTopControl(false).setSupportGesture(false)
        super.initActivity(savedState, inflater)
    }

    override fun getTabMode(adapter: PagerAdapter): Int {
        return TabLayout.MODE_SCROLLABLE
    }

    override fun setListener() {
        super.setListener()
        superPlayerView.onComplete(this)
    }

    override fun getAdapter(fm: FragmentManager): PagerAdapter {
        return ShortVideoPagerAdapter(fm)
    }

    override val activityTitle: String
        get() = getString(R.string.item_short)

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (fragmentLinearLayoutManager != null && fragmentRecyclerView != null) {
            superPlayerView.onConfigurationChanged(newConfig)
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                rl_full_screen.visibility = View.GONE
                rl_full_screen.removeAllViews()
                fragmentRecyclerView!!.visibility = View.VISIBLE
                if (position <= fragmentLinearLayoutManager!!.findLastVisibleItemPosition() && position >= fragmentLinearLayoutManager!!.findFirstVisibleItemPosition()) {
                    val view = fragmentRecyclerView!!.findViewHolderForAdapterPosition(position).itemView
                    val frameLayout = view.findViewById<View>(R.id.fl_super_video_layout) as FrameLayout
                    frameLayout.removeAllViews()
                    val last = superPlayerView.parent as ViewGroup//找到videoitemview的父类，然后remove
                    last.removeAllViews()
                    frameLayout.addView(superPlayerView)
                }
                val mShowFlags = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                rl_full_screen.systemUiVisibility = mShowFlags
            } else {
                val viewGroup = superPlayerView.parent as ViewGroup
                viewGroup.removeAllViews()
                rl_full_screen.addView(superPlayerView)
                rl_full_screen.visibility = View.VISIBLE
                val mHideFlags = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                rl_full_screen.systemUiVisibility = mHideFlags
            }
        } else {
            rl_full_screen.visibility = View.GONE
        }
    }

    override fun run() {
        if (!superPlayerView.portrait) {
            superPlayerView.onBackPressed()
            return
        }
        val last = superPlayerView.parent as ViewGroup//找到videoitemview的父类，然后remove
        if (last.childCount > 0) {
            last.removeAllViews()
            val itemView = last.parent as View
            itemView.findViewById<View>(R.id.rl_player_control).visibility = View.VISIBLE
        }
    }

    /**
     * 下面的这几个Activity的生命状态很重要
     */
    override fun onPause() {
        super.onPause()
        superPlayerView.onPause()
    }

    override fun onResume() {
        super.onResume()
        superPlayerView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        superPlayerView.onDestroy()
    }

    override fun isSwipeActivity(): Boolean {
        return false
    }

    override fun onBackPressed() {
        if (superPlayerView.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun onItemPlay(datas: List<*>, position: Int, view: RelativeLayout) {
        if (superPlayerView.isPlaying && lastPosition == position) {
            return
        }
        this@ShortVideoTabActivity.position = position
        if (superPlayerView.videoStatus == IjkVideoView.STATE_PAUSED) {
            if (position != lastPosition) {
                superPlayerView.stopPlayVideo()
                superPlayerView.release()
            }
        }
        if (lastPosition != -1) {
            superPlayerView.showView(R.id.rl_player_control)
        }
        if (fragmentRecyclerView == null) return
        val itemView = fragmentRecyclerView!!.findViewHolderForAdapterPosition(position).itemView
        val frameLayout = itemView.findViewById<View>(R.id.fl_super_video_layout) as FrameLayout
        frameLayout.removeAllViews()
        superPlayerView.showView(R.id.rl_player_control)
        frameLayout.addView(superPlayerView)
        val video = datas[position] as DyttShortVideo
        superPlayerView.setTitle(video.title)
        superPlayerView.play(video.playurl)
        lastPosition = position
        view.visibility = View.GONE
    }


    override fun onChildViewAttachedToWindow(view: View) {
        val recyclerView = fragmentRecyclerView
        val index = recyclerView?.getChildAdapterPosition(view)
        view.findViewById<View>(R.id.rl_player_control).visibility = View.VISIBLE
        if (index == position) {
            val frameLayout = view.findViewById<View>(R.id.fl_super_video_layout) as FrameLayout
            frameLayout.removeAllViews()
            if (superPlayerView.isPlaying || superPlayerView.videoStatus == IjkVideoView.STATE_PAUSED) {
                view.findViewById<View>(R.id.rl_player_control).visibility = View.GONE
            }
            if (superPlayerView.videoStatus == IjkVideoView.STATE_PAUSED) {
                if (superPlayerView.parent != null)
                    (superPlayerView.parent as ViewGroup).removeAllViews()
                frameLayout.addView(superPlayerView)
                return
            }
        }
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        val recyclerView = fragmentRecyclerView ?: return
        val index = recyclerView.getChildAdapterPosition(view)
        if (index == position) {
            superPlayerView.pause()
            superPlayerView.release()
            superPlayerView.showView(R.id.rl_player_control)
        }
    }

    companion object {

        fun startActivity(context: Context) {
            try {
                val intent = Intent(context, ShortVideoTabActivity::class.java)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}
