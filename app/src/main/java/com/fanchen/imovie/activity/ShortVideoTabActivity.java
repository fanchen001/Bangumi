package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.ShortVideoAdapter;
import com.fanchen.imovie.adapter.pager.ShortVideoPagerAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.base.BaseTabActivity;
import com.fanchen.imovie.entity.dytt.DyttShortVideo;
import com.fanchen.imovie.util.DisplayUtil;
import com.fanchen.imovie.view.video.IjkVideoView;
import com.fanchen.imovie.view.video.SuperPlayerManage;
import com.fanchen.imovie.view.video.SuperPlayerView;

import java.util.List;

import butterknife.InjectView;

/**
 * 短视频
 * Created by fanchen on 2017/9/22.
 */
public class ShortVideoTabActivity extends BaseTabActivity implements ShortVideoAdapter.
        OnItemPlayClick, Runnable, RecyclerView.OnChildAttachStateChangeListener {

    protected SuperPlayerView mSuperPlayerView;
    @InjectView(R.id.rl_full_screen)
    protected RelativeLayout mFullRelativeLayout;

    protected int position = -1;
    protected int lastPosition = -1;

    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, ShortVideoTabActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        mSuperPlayerView = SuperPlayerManage.getSuperManage().initialize(this);
        mSuperPlayerView.setShowTopControl(false).setSupportGesture(false);
        super.initActivity(savedState, inflater);
    }

    @Override
    protected int getTabMode(PagerAdapter adapter) {
        return TabLayout.MODE_SCROLLABLE;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mSuperPlayerView.onComplete(this);
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        return new ShortVideoPagerAdapter(fm);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.item_short);
    }

    public SuperPlayerView getSuperPlayerView() {
        return mSuperPlayerView;
    }

    public RelativeLayout getFullRelativeLayout() {
        return mFullRelativeLayout;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mSuperPlayerView != null && getFragmentLinearLayoutManager() != null && getFragmentRecyclerView() != null) {
            mSuperPlayerView.onConfigurationChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                mFullRelativeLayout.setVisibility(View.GONE);
                mFullRelativeLayout.removeAllViews();
                getFragmentRecyclerView().setVisibility(View.VISIBLE);
                if (position <= getFragmentLinearLayoutManager().findLastVisibleItemPosition() && position >= getFragmentLinearLayoutManager().findFirstVisibleItemPosition()) {
                    View view = getFragmentRecyclerView().findViewHolderForAdapterPosition(position).itemView;
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fl_super_video_layout);
                    frameLayout.removeAllViews();
                    ViewGroup last = (ViewGroup) mSuperPlayerView.getParent();//找到videoitemview的父类，然后remove
                    if (last != null) {
                        last.removeAllViews();
                    }
                    frameLayout.addView(mSuperPlayerView);
                }
                int mShowFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                mFullRelativeLayout.setSystemUiVisibility(mShowFlags);
            } else {
                ViewGroup viewGroup = (ViewGroup) mSuperPlayerView.getParent();
                if (viewGroup == null) return;
                viewGroup.removeAllViews();
                mFullRelativeLayout.addView(mSuperPlayerView);
                mFullRelativeLayout.setVisibility(View.VISIBLE);
                int mHideFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                mFullRelativeLayout.setSystemUiVisibility(mHideFlags);
            }
        } else {
            mFullRelativeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void run() {
        if(mSuperPlayerView == null)return;
        if(!mSuperPlayerView.getPortrait()){
            mSuperPlayerView.onBackPressed();
            return;
        }
        ViewGroup last = (ViewGroup) mSuperPlayerView.getParent();//找到videoitemview的父类，然后remove
        if (last != null && last.getChildCount() > 0) {
            last.removeAllViews();
            View itemView = (View) last.getParent();
            if (itemView != null) {
                itemView.findViewById(R.id.rl_player_control).setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 下面的这几个Activity的生命状态很重要
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mSuperPlayerView != null) {
            mSuperPlayerView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSuperPlayerView != null) {
            mSuperPlayerView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSuperPlayerView != null) {
            mSuperPlayerView.onDestroy();
        }
    }

    @Override
    protected boolean isSwipeActivity() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mSuperPlayerView != null && mSuperPlayerView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onItemPlay(List<?> datas, int position, RelativeLayout view) {
        if (mSuperPlayerView == null || mSuperPlayerView.isPlaying() && lastPosition == position) {
            return;
        }
        ShortVideoTabActivity.this.position = position;
        if (mSuperPlayerView.getVideoStatus() == IjkVideoView.STATE_PAUSED) {
            if (position != lastPosition) {
                mSuperPlayerView.stopPlayVideo();
                mSuperPlayerView.release();
            }
        }
        if (lastPosition != -1) {
            mSuperPlayerView.showView(R.id.rl_player_control);
        }
        if (getFragmentRecyclerView() == null) return;
        View itemView = getFragmentRecyclerView().findViewHolderForAdapterPosition(position).itemView;
        FrameLayout frameLayout = (FrameLayout) itemView.findViewById(R.id.fl_super_video_layout);
        frameLayout.removeAllViews();
        mSuperPlayerView.showView(R.id.rl_player_control);
        frameLayout.addView(mSuperPlayerView);
        DyttShortVideo video = (DyttShortVideo) datas.get(position);
        mSuperPlayerView.setTitle(video.getTitle());
        mSuperPlayerView.play(video.getPlayurl());
        lastPosition = position;
        view.setVisibility(View.GONE);
    }

    /**
     * @return
     */
    public RecyclerView getFragmentRecyclerView() {
        BaseRecyclerFragment visibleFragment = (BaseRecyclerFragment) getVisibleFragment();
        if (visibleFragment == null) return null;
        return visibleFragment.getRecyclerView();
    }

    /**
     * @return
     */
    public LinearLayoutManager getFragmentLinearLayoutManager() {
        BaseRecyclerFragment visibleFragment = (BaseRecyclerFragment) getVisibleFragment();
        if (visibleFragment == null) return null;
        return (LinearLayoutManager) visibleFragment.getLayoutManager();
    }


    @Override
    public void onChildViewAttachedToWindow(View view) {
        RecyclerView recyclerView = getFragmentRecyclerView();
        if(recyclerView == null || mSuperPlayerView == null)return;
        int index = recyclerView.getChildAdapterPosition(view);
        View controlview = view.findViewById(R.id.rl_player_control);
        if (controlview == null) return;
        view.findViewById(R.id.rl_player_control).setVisibility(View.VISIBLE);
        if (index == position) {
            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fl_super_video_layout);
            frameLayout.removeAllViews();
            if (mSuperPlayerView != null && ((mSuperPlayerView.isPlaying()) || mSuperPlayerView.getVideoStatus() == IjkVideoView.STATE_PAUSED)) {
                view.findViewById(R.id.rl_player_control).setVisibility(View.GONE);
            }
            if (mSuperPlayerView.getVideoStatus() == IjkVideoView.STATE_PAUSED) {
                if (mSuperPlayerView.getParent() != null)
                    ((ViewGroup) mSuperPlayerView.getParent()).removeAllViews();
                frameLayout.addView(mSuperPlayerView);
                return;
            }
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {
        RecyclerView recyclerView = getFragmentRecyclerView();
        if(recyclerView == null)return;
        int index = recyclerView.getChildAdapterPosition(view);
        if ((index) == position) {
            if (mSuperPlayerView != null) {
                mSuperPlayerView.pause();
                mSuperPlayerView.release();
                mSuperPlayerView.showView(R.id.rl_player_control);
            }
        }
    }

}
