package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.fanchen.imovie.dialog.DownloadDialog;
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
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.DisplayUtil;
import com.fanchen.imovie.util.ShareUtil;
import com.fanchen.imovie.util.VideoJsonUtil;
//import com.fanchen.imovie.util.VideoUrlUtil;
import com.fanchen.imovie.view.CustomEmptyView;
import com.fanchen.imovie.view.MaterialProgressBar;
import com.fanchen.imovie.view.RoundCornerImageView;
import com.fanchen.m3u8.M3u8Config;
import com.fanchen.m3u8.M3u8Manager;
import com.fanchen.m3u8.bean.M3u8;
import com.fanchen.m3u8.bean.M3u8File;
import com.fanchen.m3u8.listener.OnM3u8InfoListener;
import com.fanchen.sniffing.LogUtil;
import com.fanchen.sniffing.SniffingCallback;
import com.fanchen.sniffing.SniffingVideo;
import com.fanchen.sniffing.x5.SniffingUtil;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.xigua.p2p.P2PManager;
import com.xunlei.XLManager;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * 视频详情
 * Created by fanchen on 2017/8/14.
 */
public class VideoDetailsActivity extends BaseActivity implements View.OnClickListener,
        BaseAdapter.OnItemClickListener, DialogInterface.OnDismissListener,
        DownloadDialog.OnDownloadSelectListener, OnM3u8InfoListener , SniffingCallback /*, VideoUrlUtil.OnParseWebUrlListener*/ {

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
//    private VideoUrlUtil mVideoUrlUtil;
    private DownloadDialog.DownloadTemp mDownload;
    private List<DownloadDialog.DownloadTemp> mDownloads;

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
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startActivity(Context context, DialogBanner banner) {
        try {
            Intent intent = new Intent(context, VideoDetailsActivity.class);
            intent.putExtra(VIDEO_BANNWE, banner.getBaseJson());
            context.startActivity(intent);
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        M3u8Manager.INSTANCE.registerInfoListeners(this);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getIntentData(getIntent());
        mBackTitleTextView.setText(R.string.bangumi_details);
//        mVideoUrlUtil = VideoUrlUtil.getInstance().init(this);
        mEpisodeRecyclerView.setLayoutManager(new BaseAdapter.LinearLayoutManagerWrapper(this, BaseAdapter.LinearLayoutManagerWrapper.HORIZONTAL, false));
        mRecomRecyclerView.setLayoutManager(new BaseAdapter.GridLayoutManagerWrapper(this, 3));
        mEpisodeAdapter = new EpisodeAdapter(this);
        mEpisodeRecyclerView.setAdapter(mEpisodeAdapter);
        mEpisodeRecyclerView.setNestedScrollingEnabled(false);
        mRecomAdapter = new RecomAdapter(this, picasso = new PicassoWrap(getPicasso()));
        mRecomRecyclerView.setAdapter(mRecomAdapter);
        mEpisodeRecyclerView.setNestedScrollingEnabled(false);
        mRecomRecyclerView.setNestedScrollingEnabled(false);
        String path = mVideo == null ? mVideoCollect == null ? vid : mVideoCollect.getId() : mVideo.getId();
        RetrofitManager.PATH_ID = path;
        getRetrofitManager().enqueue(className, callback, "details", path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        M3u8Manager.INSTANCE.unregisterInfoListeners(this);
        SniffingUtil.get().releaseAll();
    }

    @Override
    public void onDownloadSelect(List<DownloadDialog.DownloadTemp> downloads) {
        if (downloads == null || downloads.isEmpty()) return;
        this.mDownloads = downloads;
        DialogUtil.showProgressDialog(this, getString(R.string.loading));
        download(mDownloads.remove(0));
    }

    private void download(DownloadDialog.DownloadTemp temp) {
        if (temp == null) return;
        this.mDownload = temp;
        if (this.mDownload.type == DownloadDialog.DownloadTemp.TYPE_MP4) {
            String fileNmae = details.getTitle() + "_" + temp.episode.getTitle() + ".mp4";
            String eurl = mDownload.episode.getUrl();
            String path = new File(M3u8Config.INSTANCE.getM3u8Path(), fileNmae).getAbsolutePath();
            Map<String, String> header = AppUtil.getDownloadHeader();
            getDownloadReceiver().load(temp.url).setExtendField(eurl).setFilePath(path).addHeaders(header).start();
            temp.episode.setDownloadState(IVideoEpisode.DOWNLOAD_RUN);
            showToast(String.format("<%s>添加下载任务成功", fileNmae));
            downloadNext();
        } else if (this.mDownload.type == DownloadDialog.DownloadTemp.TYPE_XIGUA) {
            P2PManager.getInstance().play(temp.url);
            temp.episode.setDownloadState(IVideoEpisode.DOWNLOAD_RUN);
            showToast(String.format("<%s>添加下载任务成功", temp.episode.getTitle()));
            downloadNext();
        } else if (this.mDownload.type == DownloadDialog.DownloadTemp.TYPE_XUNLEI) {
            XLManager.get(this).addTask(temp.url);
            temp.episode.setDownloadState(IVideoEpisode.DOWNLOAD_RUN);
            showToast(String.format("<%s>添加下载任务成功", temp.episode.getTitle()));
            downloadNext();
        } else if (this.mDownload.type == DownloadDialog.DownloadTemp.TYPE_M3U8) {
            M3u8File m3u8File = new M3u8File();
            if(temp.m3u8Url != null && temp.m3u8Url.contains("=")){
                String[] split = temp.m3u8Url.split("=");
                if(split.length == 2 && split[1].contains(".m3u")){
                    m3u8File.setUrl(split[1]);
                }else{
                    String uReplace = split[0] + "=";
                    String replace = temp.m3u8Url.replace(uReplace, "");
                    if(replace.contains(".m3u")){
                        m3u8File.setUrl(replace);
                    }else{
                        m3u8File.setUrl(temp.m3u8Url);
                    }
                }
            }else if(temp.m3u8Url != null){
                m3u8File.setUrl(temp.m3u8Url);
            }
            m3u8File.setOnlyId(temp.getOnlyId());
            m3u8File.setM3u8VideoName(String.format("%s_%s.mp4", details.getTitle(), temp.episode.getTitle()));
            M3u8Manager.INSTANCE.download(m3u8File);
        } else if (this.mDownload.type == DownloadDialog.DownloadTemp.TYPE_URL) {
            SniffingUtil.get().activity(this).url(temp.url).referer(temp.referer).callback(this).start();
//            mVideoUrlUtil.setLoadUrl(temp.url, temp.referer);
//            mVideoUrlUtil.setOnParseListener(this);
//            mVideoUrlUtil.startParse();
        } else {
            showToast(String.format("<%s>不支持下载", temp.episode.getTitle()));
            downloadNext();
        }
    }

    private void downloadNext() {
        if (mDownloads != null && mDownloads.size() > 0) {
            download(mDownloads.remove(0));
        } else {
            DialogUtil.closeProgressDialog();
            if (mEpisodeAdapter != null) mEpisodeAdapter.notifyDataSetChanged();
        }
    }

    private void getIntentData(Intent data) {
        if (data.getData() != null) {
            mVideo = VideoJsonUtil.json2Video(data.getData().getQueryParameter("info"));
            className = mVideo == null ? "" : mVideo.getServiceClass();
        }
        if (getIntent().hasExtra(VIDEO_BANNWE)) {
            mVideo = VideoJsonUtil.json2Video(data.getStringExtra(VIDEO_BANNWE));
            className = mVideo == null ? "" : mVideo.getServiceClass();
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
    }

    @Override
    public void onSniffingSuccess(View webView, String webUrl, List<SniffingVideo> videos) {
        if (mDownload == null || details == null || videos.isEmpty()) return;
        String url = videos.get(0).getUrl();
        String title = mDownload.episode.getTitle();
        String format = String.format("%s_%s.mp4", details.getTitle(), title);
        if (url.contains(".m3u")) {
            M3u8File m3u8File = new M3u8File();
            if(url.contains("=") && url.split("=")[1].contains(".m3u")){
                m3u8File.setUrl(url.split("=")[1]);
            }else{
                m3u8File.setUrl(url);
            }
            LogUtil.e("m3u8Url 3","====>" + url);
            m3u8File.setOnlyId(mDownload.getOnlyId());
            m3u8File.setM3u8VideoName(format);
            M3u8Manager.INSTANCE.download(m3u8File);
        } else if (url.contains(".rm") || url.contains(".mp4") || url.contains(".avi") || url.contains(".wmv")) {
            mDownload.episode.setDownloadState(IVideoEpisode.DOWNLOAD_RUN);
            String eurl = mDownload.episode.getUrl();
            String path = new File(M3u8Config.INSTANCE.getM3u8Path(), format).getAbsolutePath();
            Map<String, String> header = AppUtil.getDownloadHeader();
            getDownloadReceiver().load(url).setExtendField(eurl).setFilePath(path).addHeaders(header).start();
            showToast(String.format("<%s>添加下载任务成功", format));
            downloadNext();
        } else {
            showToast(String.format("<%s>不支持下载", format));
            downloadNext();
        }
    }

    @Override
    public void onSniffingError(View webView, String url, int errorCode) {
        if (mDownload == null || mDownload.episode == null || details == null) return;
        String format = String.format("%s_%s.mp4", details.getTitle(), mDownload.episode.getTitle());
        showToast(String.format("<%s>解析M3u8任务失败", format));
        downloadNext();
    }

//    @Override
//    public void onFindUrl(String url) {
//        if (mDownload == null || details == null) return;
//        String title = mDownload.episode.getTitle();
//        String format = String.format("%s_%s.mp4", details.getTitle(), title);
//        if (url.contains(".m3u")) {
//            M3u8File m3u8File = new M3u8File();
//            if(url.contains("=") && url.split("=")[1].contains(".m3u")){
//                m3u8File.setUrl(url.split("=")[1]);
//            }else{
//                m3u8File.setUrl(url);
//            }
//            m3u8File.setOnlyId(mDownload.getOnlyId());
//            m3u8File.setM3u8VideoName(format);
//            M3u8Manager.INSTANCE.download(m3u8File);
//        } else if (url.contains(".rm") || url.contains(".mp4") || url.contains(".avi") || url.contains(".wmv")) {
//            mDownload.episode.setDownloadState(IVideoEpisode.DOWNLOAD_RUN);
//            String eurl = mDownload.episode.getUrl();
//            String path = new File(M3u8Config.INSTANCE.getM3u8Path(), format).getAbsolutePath();
//            Map<String, String> header = AppUtil.getDownloadHeader();
//            getDownloadReceiver().load(url).setExtendField(eurl).setFilePath(path).addHeaders(header).start();
//            showToast(String.format("<%s>添加下载任务成功", format));
//            downloadNext();
//        } else {
//            showToast(String.format("<%s>不支持下载", format));
//            downloadNext();
//        }
//    }
//
//    @Override
//    public void onError(String errorMsg) {
//        if (mDownload == null || mDownload.episode == null || details == null) return;
//        String format = String.format("%s_%s.mp4", details.getTitle(), mDownload.episode.getTitle());
//        showToast(String.format("<%s>解析M3u8任务失败", format));
//        downloadNext();
//    }

    @Override
    public void onSuccess(M3u8File m3u8File, List<M3u8> list) {
        if (mDownload == null || mDownload.episode == null) return;
        mDownload.episode.setDownloadState(IVideoEpisode.DOWNLOAD_RUN);
        M3u8Manager.INSTANCE.download(list);
        showToast(String.format("<%s>添加下载任务成功", m3u8File.getM3u8VideoName()));
        downloadNext();
    }

    @Override
    public void onError(M3u8File m3u8File, Throwable throwable) {
        showToast(String.format("<%s>下载失败", m3u8File.getM3u8VideoName()));
        downloadNext();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cev_empty) {
            String path = mVideo == null ? mVideoCollect == null ? vid : mVideoCollect.getId() : mVideo.getId();
            RetrofitManager.PATH_ID = path;
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
                    String format = String.format(getString(R.string.collect_hit), details.getTitle());
                    DialogUtil.showMaterialDialog(this, format, buttonClickListener);
                }
                break;
            case R.id.ll_bangumi_share:
                try {
//                     ShareUtil.shareDialogBanner(details,20180910);
                    PackageManager manager = getPackageManager();
                    List<ApplicationInfo> infos = manager.getInstalledApplications(128);
                    if (infos == null || infos.isEmpty()) {
                        showToast("获取可分享应用信息失败,请给应用授权");
                    } else {
                        Uri.Builder info = Uri.parse("https://details").buildUpon().appendQueryParameter("info", new Gson().toJson(details));
                        ShareUtil.share(this, details.getTitle(), details.getIntroduce(), info.toString());
                    }
                } catch (Throwable e) {
                    showToast(String.format("分享失败 <%s>", e.toString()));
                }
                break;
            case R.id.ll_bangumi_download:
                if (details.canDownload()) {
                    DialogUtil.showDownloadDialog(this, details, this);
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
                    VideoPlayerActivity.startActivity(this, episode.getUrl());
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
        if (mEpisodeAdapter != null) mEpisodeAdapter.notifyDataSetChanged();
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
            if (response == null || !response.isSuccess() || mRecomAdapter == null) {
                onFailure(enqueueKey, "未知錯誤");
                return;
            }
            details = mVideo == null ? mVideoCollect == null ? response : response.setVideo(mVideoCollect) : response.setVideo(mVideo);
            if (!TextUtils.isEmpty(response.getCoverReferer())) {
                picasso = new PicassoWrap(VideoDetailsActivity.this, new RefererDownloader(getApplicationContext(), response.getCoverReferer()));
                mRecomAdapter.setPicasso(picasso);
            }
            picasso.loadVertical(response.getCover(), VideoDetailsActivity.class, false, mRoundImageView);
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

}
