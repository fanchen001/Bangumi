package com.fanchen.imovie.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arialyy.aria.core.download.DownloadEntity;
import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.ApkScreenAdapter;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.dialog.ShowImagesDialog;
import com.fanchen.imovie.entity.apk.ApkDetails;
import com.fanchen.imovie.entity.apk.ApkItem;
import com.fanchen.imovie.entity.apk.ApkParamData;
import com.fanchen.imovie.entity.apk.ApkParamUser;
import com.fanchen.imovie.entity.apk.ApkRoot;
import com.fanchen.imovie.entity.apk.ApkVideo;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.callback.RetrofitCallback;
import com.fanchen.imovie.retrofit.service.MoeapkService;
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.DisplayUtil;
import com.fanchen.imovie.view.CustomEmptyView;
import com.fanchen.imovie.view.EnabledScrollView;
import com.fanchen.imovie.view.MaterialProgressBar;
import com.fanchen.imovie.view.callback.AppBarStateChangeListener;
import com.fanchen.imovie.view.video.SuperPlayerView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.List;

import butterknife.InjectView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 應用，遊戲詳情頁面
 * Created by fanchen on 2017/8/5.
 */
public class ApkDetailsActivity extends BaseActivity implements View.OnClickListener,
        BaseAdapter.OnItemClickListener {

    public static final int DIP_80 = 80;

    public static final String APK_ITEM = "apk";

    @InjectView(R.id.spv_video)
    protected SuperPlayerView mSuperPlayerView;
    @InjectView(R.id.iv_game_cover)
    protected ImageView mCoverImageView;
    @InjectView(R.id.iv_bar_back)
    protected ImageView mBackView;
    @InjectView(R.id.tv_bar_title)
    protected TextView mBarTitleTextView;
    @InjectView(R.id.tv_bar_title_play)
    protected TextView mBarPlayView;
    @InjectView(R.id.nav_top_bar)
    Toolbar mToolbar;
    @InjectView(R.id.ctl_game)
    protected CollapsingToolbarLayout mToolbarLayout;
    @InjectView(R.id.iv_icon)
    protected ImageView mIconImageView;
    @InjectView(R.id.tv_title)
    protected TextView mTitleTextView;
    @InjectView(R.id.tv_intor)
    protected TextView mIntorTextView;
    @InjectView(R.id.tv_app_description)
    protected TextView mDescriptionTextView;
    @InjectView(R.id.tv_app_more_info)
    protected TextView mMoreTextView;
    @InjectView(R.id.tv_app_package_name)
    protected TextView mPackageNameTextView;
    @InjectView(R.id.tv_app_update_time)
    protected TextView mUpdateTextView;
    @InjectView(R.id.tv_app_mark)
    protected TextView mMarkTextView;
    @InjectView(R.id.rv_screen)
    protected RecyclerView mRecyclerView;
    @InjectView(R.id.nsv_game)
    protected EnabledScrollView mNestedScrollView;
    @InjectView(R.id.fab_apk_play)
    protected FloatingActionButton mPlayFab;
    @InjectView(R.id.fab_apk_download)
    protected FloatingActionButton mDownloadButton;
    @InjectView(R.id.progressbar_apk)
    protected MaterialProgressBar mProgressbar;
    @InjectView(R.id.cev_empty)
    CustomEmptyView mEmptyView;
    @InjectView(R.id.abl_game)
    View mAblView;

    protected View mainView;


    private ApkItem apkItem;
    private ApkDetails mApkDetails;
    private Picasso picasso;
    private Gson gson = new Gson();
    private ApkScreenAdapter mApkScreenAdapter;
    private SuperPlayerCallBack playerCallBack = new SuperPlayerCallBack();

    /**
     * @param activity
     * @param item
     */
    public static void startActivity(Activity activity, ApkItem item) {
        try {
            Intent intent = new Intent(activity, ApkDetailsActivity.class);
            intent.putExtra(APK_ITEM, item);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_apk_details;
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        mainView = getMainView();
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        apkItem = getIntent().getParcelableExtra(APK_ITEM);
        mApkScreenAdapter = new ApkScreenAdapter(this, picasso = getPicasso());
        mRecyclerView.setLayoutManager(new BaseAdapter.LinearLayoutManagerWrapper(this, BaseAdapter.LinearLayoutManagerWrapper.HORIZONTAL, false));
        mRecyclerView.setAdapter(mApkScreenAdapter);
        mSuperPlayerView.setNetChangeListener(true);
        mSuperPlayerView.setShowTopControl(false);
        mSuperPlayerView.setScaleType(SuperPlayerView.SCALETYPE_FILLPARENT);
        mSuperPlayerView.setPlayerWH(mSuperPlayerView.getMeasuredWidth(), mSuperPlayerView.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
        //加载数据
        getRetrofitManager().enqueue(MoeapkService.class, detailsCallback, "details", createBody());
    }

    @Override
    protected void setListener() {
        super.setListener();
        mEmptyView.setOnClickListener(this);
        mMoreTextView.setOnClickListener(this);
        mBackView.setOnClickListener(this);
        mPlayFab.setOnClickListener(this);
        mBarPlayView.setOnClickListener(this);
        mDownloadButton.setOnClickListener(this);
        mSuperPlayerView.onComplete(playerCallBack);
        mSuperPlayerView.setOnNetChangeListener(playerCallBack);
        mSuperPlayerView.setOnPlayStateChangeListener(playerCallBack);
        mApkScreenAdapter.setOnItemClickListener(this);
        ((AppBarLayout) mToolbar.getParent().getParent()).addOnOffsetChangedListener(appBarListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_apk, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_translate:
                break;
            case R.id.action_share:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 创建请求参数
     *
     * @return
     */
    private RequestBody createBody() {
        String data = gson.toJson(new ApkParamData(apkItem.getPackagename()));
        String user = gson.toJson(new ApkParamUser());
        String format = String.format("data=%s&user=%s", URLEncoder.encode(data), URLEncoder.encode(user));
        return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), format);
    }

    /**
     * 设置推广视频信息
     *
     * @param video
     */
    private void fillVideoData(ApkVideo video) {
        if (mPlayFab == null || mSuperPlayerView == null) return;
        //有推广视频
        mPlayFab.setVisibility(View.VISIBLE);
        mSuperPlayerView.playUrl(video.getBest());//设置视频的titleName
    }

    /**
     * 填充数据
     *
     * @param details
     */
    private void fillViewData(ApkDetails details) {
        this.mApkDetails = details;
        mBarTitleTextView.setText(details.getTitle());
        mSuperPlayerView.setTitle(details.getTitle());
        if (!TextUtils.isEmpty(details.getCover()))
            picasso.load(details.getCover())
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.image_load_pre)
                    .error(R.drawable.image_load_error).into(mCoverImageView);//背景
        if (!TextUtils.isEmpty(details.getIco()))
            picasso.load(details.getIco()).config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.image_load_icon_pre)
                    .error(R.drawable.image_load_icon_error).into(mIconImageView);//图标
        mBarTitleTextView.setText(details.getTitle());
        mTitleTextView.setText(details.getTitle());
        mIntorTextView.setText(Html.fromHtml(details.getIntro()));
        mDescriptionTextView.setText(Html.fromHtml(details.getDescription()));
        mMarkTextView.setText(Html.fromHtml(details.getRecentchanges()));
        mUpdateTextView.setText(details.getUpdatetime());
        mPackageNameTextView.setText(details.getPackagename());
        mApkScreenAdapter.setList(details.getScreenShots());
    }

    /**
     *
     */
    private void togoMoreInfo() {
        ViewGroup.LayoutParams layoutParams = mDescriptionTextView.getLayoutParams();
        if (mDescriptionTextView.getHeight() == DisplayUtil.dip2px(this, DIP_80)) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mDescriptionTextView.setLayoutParams(layoutParams);
            mMoreTextView.setText(R.string.more_info_sq);
        } else {
            layoutParams.height = DisplayUtil.dip2px(this, DIP_80);
            mDescriptionTextView.setLayoutParams(layoutParams);
            mMoreTextView.setText(R.string.more_info);
        }
    }

    /**
     *
     */
    private void playVideo() {
        if (mSuperPlayerView == null && TextUtils.isEmpty(mSuperPlayerView.getUrl())) return;
        mCoverImageView.setVisibility(View.GONE);
        if (mSuperPlayerView.isStop()) {
            //开始播放
            mSuperPlayerView.start();
        } else {
            //从头开始播放
            mSuperPlayerView.play();//开始播放视频
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cev_empty:
                getRetrofitManager().enqueue(MoeapkService.class, detailsCallback, "details", createBody());
                break;
            case R.id.fab_apk_play:
                playVideo();
                break;
            case R.id.fab_apk_download:
                if (mApkDetails == null) return;
                String format = String.format("https://api.apk.moe/client/app/downloadApk?package=%s", mApkDetails.getPackagename());
                if (!TextUtils.isEmpty(format)) {
                    if (getDownloadReceiver().taskExists(format)) {
                        showSnackbar(getString(R.string.task_exists));
                    } else {
                        String apkPath = AppUtil.getApkPath(this);
                        if (TextUtils.isEmpty(apkPath)) return;
                        if(!format.startsWith("http") && !format.startsWith("ftp"))return;
                        getDownloadReceiver().load(format).setDownloadPath(apkPath + " /" + mApkDetails.getPackagename() + ".apk").start();
                        showSnackbar(getString(R.string.download_add));
                    }
                }
                break;
            case R.id.iv_bar_back:
                finish();
                break;
            case R.id.tv_bar_title_play:
                mPlayFab.callOnClick();
                break;
            case R.id.tv_app_more_info:
                togoMoreInfo();
                break;
        }
    }


    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof ShowImagesDialog.IPhotoImage)) return;
        ShowImagesDialog.showDialog(this, (List<ShowImagesDialog.IPhotoImage>) datas, position);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mSuperPlayerView != null) {
            mSuperPlayerView.onConfigurationChanged(newConfig);
            if (!DisplayUtil.isScreenChange(this)) {
                //全屏播放
                ((ViewGroup) mSuperPlayerView.getParent()).removeView(mSuperPlayerView);
                mToolbarLayout.addView(mSuperPlayerView, 0);
                setContentView(mainView);
            } else if (mainView != null) {
                //非全屏播放
                mToolbarLayout.removeView(mSuperPlayerView);
                setContentView(mSuperPlayerView);
            }
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

    private class SuperPlayerCallBack implements Runnable, SuperPlayerView.OnNetChangeListener, SuperPlayerView.OnPlayStateChangeListener {

        @Override
        public void run() {
            //监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
            if (DisplayUtil.isScreenChange(ApkDetailsActivity.this)) {
                //横屏播放完成之后退出横屏
                onBackPressed();
            }
        }

        /**
         * 网络链接监听类
         */
        @Override
        public void onWifi() {
            showToast("当前网络环境是WIFI");
        }

        @Override
        public void onMobile() {
            showToast("当前网络环境是手机网络");
        }

        @Override
        public void onDisConnect() {
            showToast("网络链接断开");
        }

        @Override
        public void onNoAvailable() {
            showToast("无网络链接");
        }

        @Override
        public void onStateChange(int state) {
            if (DisplayUtil.isScreenChange(ApkDetailsActivity.this)) return;
            if (state == SuperPlayerView.STATUS_PLAYING) {
                //播放状态下按钮不可见，视图不可以滚动
                mPlayFab.setVisibility(View.GONE);
                mToolbar.setVisibility(View.GONE);
                mSuperPlayerView.setIsShowBottomControl(true);
                mNestedScrollView.setNestedScrollingEnabled(false);
                getMainView().setEnabled(false);
            } else {
                //暂停等状态下，可以滚动屏幕
                getMainView().setEnabled(true);
                mNestedScrollView.setNestedScrollingEnabled(true);
                mPlayFab.setVisibility(View.VISIBLE);
                mToolbar.setVisibility(View.VISIBLE);
                mSuperPlayerView.setIsShowBottomControl(false);
            }
        }
    }

    private AppBarStateChangeListener appBarListener = new AppBarStateChangeListener() {

        @Override
        public void onStateChanged(AppBarLayout appBarLayout, State state) {
            if (!mSuperPlayerView.isStop()) return;
            if (state == State.EXPANDED) {
                //展开状态
                mBarPlayView.setVisibility(View.INVISIBLE);
                mBarTitleTextView.setVisibility(View.VISIBLE);
                if (mSuperPlayerView.getVisibility() == View.INVISIBLE) {
                    mSuperPlayerView.setVisibility(View.VISIBLE);
                    mCoverImageView.setVisibility(View.GONE);
                }
            } else if (state == State.COLLAPSED) {
                //折叠状态
                mBarPlayView.setVisibility(View.VISIBLE);
                mBarTitleTextView.setVisibility(View.INVISIBLE);
            } else {
                //中间状态
                mCoverImageView.setVisibility(View.VISIBLE);
                mSuperPlayerView.setVisibility(View.INVISIBLE);
            }
        }

    };

    private RefreshCallback<ApkRoot<ApkDetails>> detailsCallback = new RefreshCallback<ApkRoot<ApkDetails>>() {

        @Override
        public void onStart(int enqueueKey) {
            if (mProgressbar != null)
                mProgressbar.setVisibility(View.VISIBLE);
            if (mEmptyView != null)
                mEmptyView.setEmptyType(CustomEmptyView.TYPE_NON);
            if (mAblView != null)
                mAblView.setVisibility(View.VISIBLE);
            if (mNestedScrollView != null)
                mNestedScrollView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish(int enqueueKey) {
            if (mProgressbar != null)
                mProgressbar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            if (mEmptyView != null)
                mEmptyView.setEmptyType(CustomEmptyView.TYPE_ERROR);
            if (mAblView != null)
                mAblView.setVisibility(View.GONE);
            if (mNestedScrollView != null)
                mNestedScrollView.setVisibility(View.GONE);
            showSnackbar(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, ApkRoot<ApkDetails> response) {
            if (response == null || response.getData() == null || mApkScreenAdapter == null) return;
            ApkDetails data = response.getData();
            fillViewData(data);
            //apk有推广视频
            if (data.getHasvideo() == ApkDetails.HAS_VIDEO) {
                getRetrofitManager().enqueue(MoeapkService.class, videoCallback, "videoUrl", createBody());
            }
        }

    };

    private RetrofitCallback<ApkRoot<ApkVideo>> videoCallback = new RetrofitCallback<ApkRoot<ApkVideo>>() {

        @Override
        public void onSuccess(int enqueueKey, ApkRoot<ApkVideo> response) {
            if (response == null || response.getData() == null || mApkScreenAdapter == null) return;
            fillVideoData(response.getData());
        }

    };
}
