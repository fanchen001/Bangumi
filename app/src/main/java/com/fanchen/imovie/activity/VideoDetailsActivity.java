package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.EpisodeAdapter;
import com.fanchen.imovie.adapter.RecomAdapter;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.bmob.BmobObj;
import com.fanchen.imovie.entity.bmob.DialogBanner;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.bmob.VideoCollect;
import com.fanchen.imovie.picasso.download.RefererDownloader;
import com.fanchen.imovie.picasso.trans.BlurTransform;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.DisplayUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.SecurityUtil;
import com.fanchen.imovie.util.ShareUtil;
import com.fanchen.imovie.util.SystemUtil;
import com.fanchen.imovie.view.CustomEmptyView;
import com.fanchen.imovie.view.MaterialProgressBar;
import com.fanchen.imovie.view.RoundCornerImageView;
import com.google.gson.Gson;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import butterknife.InjectView;

/**
 * 视频详情
 * Created by fanchen on 2017/8/14.
 */
public class VideoDetailsActivity extends BaseActivity implements
        View.OnClickListener, BaseAdapter.OnItemClickListener, DialogInterface.OnDismissListener {

    public static final String VIDEO = "video";
    public static final String COLLECT = "collect";
    public static final String VID = "vid";
    public static final String VIDEO_BANNWE = "video_bannwe";
    public static final String CLASS_NAME = "className";

    @InjectView(R.id.iv_bangumi_image)
    ImageView mBackgroudImageView;
    @InjectView(R.id.iv_bangumi_cover)
    RoundCornerImageView mRoundImageView;
    @InjectView(R.id.tv_bangumi_title)
    TextView mTitleTextView;
    @InjectView(R.id.tv_bangumi_area)
    TextView mAreaTextView;
    @InjectView(R.id.tv_bangumi_type)
    TextView mTypeTextView;
    @InjectView(R.id.ll_bangumi_collect)
    LinearLayout mCollectLinearLayout;
    @InjectView(R.id.ll_bangumi_share)
    LinearLayout mShareLinearLayout;
    @InjectView(R.id.ll_bangumi_download)
    LinearLayout mDownloadLinearLayout;
    @InjectView(R.id.iv_top_back)
    ImageView mBackImageView;
    @InjectView(R.id.tv_top_title)
    TextView mBackTitleTextView;
    @InjectView(R.id.toolbar_top)
    Toolbar mToolbar;
    @InjectView(R.id.tv_bangumi_more_episode)
    TextView mMoreEpisodeTextView;
    @InjectView(R.id.recyclerview_episode)
    RecyclerView mEpisodeRecyclerView;
    @InjectView(R.id.tv_bangumi_more_info)
    TextView mMoreInfoTextView;
    @InjectView(R.id.tv_bangumi_info)
    TextView mInfoTextView;
    @InjectView(R.id.tv_video_recom)
    TextView mRecomTextView;
    @InjectView(R.id.recyclerview_recom)
    RecyclerView mRecomRecyclerView;
    @InjectView(R.id.progressbar_apk)
    MaterialProgressBar mMaterialProgressBar;
    @InjectView(R.id.tv_non_recom)
    TextView mNonTextView;
    @InjectView(R.id.tv_non_episode)
    TextView mNonEpisodeTextView;
    @InjectView(R.id.cev_empty)
    CustomEmptyView mEmptyView;
    @InjectView(R.id.abl_root)
    View mAblView;
    @InjectView(R.id.nsv_root)
    View mNsvView;

    private IVideo mVideo;
    private VideoCollect mVideoCollect;
    private String className;
    private String vid;
    private IVideoDetails details;
    private PicassoWrap picasso;
    private EpisodeAdapter mEpisodeAdapter;
    private RecomAdapter mRecomAdapter;

    /**
     * @param context
     * @param id
     * @param className
     */
    public static void startActivity(Context context, String id, String className) {
        try {
            Intent intent = new Intent(context, VideoDetailsActivity.class);
            intent.putExtra(VID, id);
            intent.putExtra(CLASS_NAME, className);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param item
     */
    public static void startActivity(Context context, IVideo item, String className) {
        try {
            Intent intent = new Intent(context, VideoDetailsActivity.class);
            intent.putExtra(VIDEO, item);
            intent.putExtra(CLASS_NAME, className);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void startActivity(Context context, DialogBanner banner) {
        try {
            Intent intent = new Intent(context, VideoDetailsActivity.class);
            intent.putExtra(VIDEO_BANNWE, banner.getBaseJson());
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param videoCollect
     */
    public static void startActivity(Context context, VideoCollect videoCollect) {
        try {
            Intent intent = new Intent(context, VideoDetailsActivity.class);
            intent.putExtra(COLLECT, (Parcelable) videoCollect);
            intent.putExtra(CLASS_NAME, videoCollect.getServiceClassName());
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param item
     */
    public static void startActivity(Context context, IVideo item) {
        startActivity(context, item, item.getServiceClass());
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_video_details;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mEmptyView.setOnClickListener(this);
        mCollectLinearLayout.setOnClickListener(this);
        mShareLinearLayout.setOnClickListener(this);
        mDownloadLinearLayout.setOnClickListener(this);
        mBackImageView.setOnClickListener(this);
        mMoreInfoTextView.setOnClickListener(this);
        mMoreEpisodeTextView.setOnClickListener(this);
        mEpisodeAdapter.setOnItemClickListener(this);
        mRecomAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mBackTitleTextView.setText(R.string.bangumi_details);
        if (getIntent().getData() != null) {
            String info = getIntent().getData().getQueryParameter("info");
            try {
                JSONObject jsonObject = new JSONObject(info);
                if (jsonObject.has("thisClass")) {
                    Class<?> forName = Class.forName(jsonObject.getString("thisClass"));
                    mVideo = (IVideo) new Gson().fromJson(info, forName);
                    className = mVideo.getServiceClass();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(getIntent().hasExtra(VIDEO_BANNWE)){
            try {
                String decode = getIntent().getStringExtra(VIDEO_BANNWE);
                JSONObject jsonObject = new JSONObject(decode);
                if (jsonObject.has("thisClass")) {
                    Class<?> forName = Class.forName(jsonObject.getString("thisClass"));
                    mVideo = (IVideo) new Gson().fromJson(decode, forName);
                    className = mVideo.getServiceClass();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (getIntent().hasExtra(VIDEO)) {
            mVideo = getIntent().getParcelableExtra(VIDEO);
        }
        if (getIntent().hasExtra(COLLECT)) {
            mVideoCollect = getIntent().getParcelableExtra(COLLECT);
        }
        if (getIntent().hasExtra(VID)) {
            vid = getIntent().getStringExtra(VID);
        }
        if (getIntent().hasExtra(CLASS_NAME)) {
            className = getIntent().getStringExtra(CLASS_NAME);
        }
        mEpisodeRecyclerView.setLayoutManager(new BaseAdapter.LinearLayoutManagerWrapper(this, BaseAdapter.LinearLayoutManagerWrapper.HORIZONTAL, false));
        mRecomRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mEpisodeAdapter = new EpisodeAdapter(this);
        mEpisodeRecyclerView.setAdapter(mEpisodeAdapter);
        mEpisodeRecyclerView.setNestedScrollingEnabled(false);
        mRecomAdapter = new RecomAdapter(this, picasso = new PicassoWrap(getPicasso()));
        mRecomRecyclerView.setAdapter(mRecomAdapter);
        mRecomRecyclerView.setNestedScrollingEnabled(false);
        String path = mVideo == null ? mVideoCollect == null ? vid : mVideoCollect.getId() : mVideo.getId();
        getRetrofitManager().enqueue(className, callback, "details", path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cev_empty) {
            String path = mVideo == null ? mVideoCollect == null ? vid : mVideoCollect.getId() : mVideo.getId();
            getRetrofitManager().enqueue(className, callback, "details", path);
            return;
        } else if (v.getId() == R.id.iv_top_back) {
            finish();
            return;
        }
        if (details == null) return;
        switch (v.getId()) {
            case R.id.tv_bangumi_more_episode:
                if (details.getEpisodes() == null || details.getEpisodes().size() == 0) {
                    showSnackbar(getString(R.string.non_episode));
                } else {
                    EpisodeActivity.startActivity(this, details);
                }
                break;
            case R.id.ll_bangumi_collect:
                if (checkLogin()) {
                    DialogUtil.showMaterialDialog(this, String.format(getString(R.string.collect_hit), details.getTitle()), buttonClickListener);
                }
                break;
            case R.id.ll_bangumi_share:
                Uri.Builder info = Uri.parse("https://details").buildUpon().appendQueryParameter("info", new Gson().toJson(details));
                ShareUtil.share(this, details.getTitle(), details.getIntroduce(), info.toString());
//
//                DialogBanner banner = new DialogBanner();
//                banner.setTitle(details.getTitle());
//                banner.setCover(details.getCover());
//                banner.setBannerInt(20180803);
//                banner.setBaseJson(new Gson().toJson(details));
//                banner.setIntroduce(details.getIntroduce());
//                banner.save();
                break;
            case R.id.ll_bangumi_download:
                if (details.canDownload()) {
                    DialogUtil.showDownloadDialog(this, details);
                } else {
                    showSnackbar(getString(R.string.not_download));
                }
                break;
            case R.id.tv_bangumi_more_info:
                int dip2px = DisplayUtil.dip2px(this, 34);
                ViewGroup.LayoutParams layoutParams = mInfoTextView.getLayoutParams();
                if (layoutParams.height == dip2px) {
                    mMoreInfoTextView.setText(getString(R.string.close_more_jianjie));
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    mMoreInfoTextView.setText(getString(R.string.more_jianjie));
                    layoutParams.height = dip2px;
                }
                mInfoTextView.setLayoutParams(layoutParams);
                break;
        }
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (details == null || v == null || v.getParent() == null) return;
        switch (((View) v.getParent()).getId()) {
            case R.id.recyclerview_recom:
                if (!(datas.get(position) instanceof IVideo)) return;
                IVideo iVideo = (IVideo) datas.get(position);
                VideoDetailsActivity.startActivity(this, iVideo, className);
                break;
            case R.id.recyclerview_episode:
                if (!(datas.get(position) instanceof IVideoEpisode)) return;
                IVideoEpisode episode = (IVideoEpisode) datas.get(position);
                if (episode.getPlayerType() == IVideoEpisode.PLAY_TYPE_XUNLEI) {
                    showSnackbar(getString(R.string.video_xunlei));
                    SystemUtil.startThreeApp(this, episode.getUrl());
                } else if (episode.getPlayerType() == IVideoEpisode.PLAY_TYPE_NOT) {
                    showSnackbar(getString(R.string.video_not_play));
                } else {
                    VideoPlayerActivity.startActivity(this, details, episode);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mEpisodeAdapter != null)
            mEpisodeAdapter.notifyDataSetChanged();
    }

    private RefreshCallback<IVideoDetails> callback = new RefreshCallback<IVideoDetails>() {

        @Override
        public void onStart(int enqueueKey) {
            if (mMaterialProgressBar != null)
                mMaterialProgressBar.setVisibility(View.VISIBLE);
            if (mEmptyView != null)
                mEmptyView.setEmptyType(CustomEmptyView.TYPE_NON);
            if (mAblView != null)
                mAblView.setVisibility(View.VISIBLE);
            if (mNsvView != null)
                mNsvView.setVisibility(View.VISIBLE);

        }

        @Override
        public void onFinish(int enqueueKey) {
            if (mMaterialProgressBar != null)
                mMaterialProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            if (mEmptyView != null)
                mEmptyView.setEmptyType(CustomEmptyView.TYPE_ERROR);
            if (mAblView != null)
                mAblView.setVisibility(View.GONE);
            if (mNsvView != null)
                mNsvView.setVisibility(View.GONE);
            showSnackbar(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, IVideoDetails response) {
            if (response == null || !response.isSuccess() || mRecomAdapter == null){
                onFailure(enqueueKey,"未知錯誤");
                return;
            }
            details = mVideo == null ? mVideoCollect == null ? response : response.setVideo(mVideoCollect) : response.setVideo(mVideo);
            if (!TextUtils.isEmpty(response.getCoverReferer())) {
                picasso = new PicassoWrap(VideoDetailsActivity.this, new RefererDownloader(getApplicationContext(), response.getCoverReferer()));
                mRecomAdapter.setPicasso(picasso);
            }
            picasso.loadVertical(response.getCover(), VideoDetailsActivity.class,false, mRoundImageView);
            if (!TextUtils.isEmpty(response.getCover())) {
                picasso.getPicasso().load(response.getCover()).transform(new BlurTransform()).into(mBackgroudImageView);
            }
            mTitleTextView.setText(response.getTitle());
            mAreaTextView.setText(response.getExtras());
            mTypeTextView.setText(response.getLast());
            mInfoTextView.setText(response.getIntroduce());
            List<? extends IVideoEpisode> episodes = response.getEpisodes();
            if (episodes == null || episodes.size() == 0) {
                mNonEpisodeTextView.setVisibility(View.VISIBLE);
            } else {
                mNonEpisodeTextView.setVisibility(View.GONE);
                mEpisodeAdapter.addAll(episodes);
            }
            List<? extends IVideo> recoms = response.getRecoms();
            if (recoms == null || recoms.size() == 0) {
                mNonTextView.setVisibility(View.VISIBLE);
            } else {
                mNonTextView.setVisibility(View.GONE);
                mRecomAdapter.addAll(recoms);
            }
        }

    };

    /**
     *
     */
    private OnButtonClickListener buttonClickListener = new OnButtonClickListener() {

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            if (btn == OnButtonClickListener.RIGHT && details != null) {
                long count = getLiteOrm().queryCount(new QueryBuilder<>(VideoCollect.class).where("id = ?", details.getId()));
                if (count <= 0) {
                    VideoCollect collect = new VideoCollect(details);
                    collect.save(new CollectListener(collect));
                } else {
                    showSnackbar(getString(R.string.collect_repetition));
                }
            }
            dialog.dismiss();
        }

    };

    private class CollectListener extends BmobObj.OnRefreshListener {

        private VideoCollect collect;

        public CollectListener(VideoCollect collect) {
            this.collect = collect;
        }

        @Override
        public void onStart() {
            if (isFinishing()) return;
            DialogUtil.showProgressDialog(VideoDetailsActivity.this, getString(R.string.collect_ing));
        }

        @Override
        public void onFinish() {
            if (isFinishing()) return;
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onSuccess() {
            AsyTaskQueue.newInstance().execute(new SaveTaskListener(collect));
            if (isFinishing()) return;
            showSnackbar(getString(R.string.collect_asy_success));
        }

        @Override
        public void onFailure(int i, String s) {
            if (isFinishing()) return;
            showSnackbar(getString(R.string.collect_asy_error));
        }

    }

    ;

    /**
     *
     */
    private class SaveTaskListener extends AsyTaskListenerImpl<Integer> {

        public int SUCCESS = 0;
        public int ERROR = 2;

        private VideoCollect collect;

        public SaveTaskListener(VideoCollect collect) {
            this.collect = collect;
        }

        @Override
        public Integer onTaskBackground() {
            if (details == null || getLiteOrm() == null) return ERROR;
            getLiteOrm().insert(collect);
            return SUCCESS;
        }

        @Override
        public void onTaskSuccess(Integer data) {
            if (data == SUCCESS) {
                showSnackbar(getString(R.string.collect_success));
            } else {
                showSnackbar(getString(R.string.collect_error));
            }
        }

    }

    ;

}
