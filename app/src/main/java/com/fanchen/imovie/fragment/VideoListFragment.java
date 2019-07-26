package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.LivePlayerActivity;
import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.adapter.VideoIndexAdapter;
import com.fanchen.imovie.adapter.VideoListAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.picasso.PicassoListener;
import com.fanchen.imovie.picasso.download.RefererDownloader;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.view.pager.LoopViewPager;
import com.fanchen.m3u8.M3u8Config;
import com.fanchen.m3u8.M3u8Manager;
import com.fanchen.m3u8.bean.M3u8;
import com.fanchen.m3u8.bean.M3u8File;
import com.fanchen.m3u8.listener.OnM3u8InfoListener;
import com.fanchen.sniffing.SniffingCallback;
import com.fanchen.sniffing.SniffingVideo;
import com.fanchen.sniffing.x5.SniffingUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * VideoListFragment
 * Created by fanchen on 2017/9/23.
 */
public class VideoListFragment extends BaseRecyclerFragment implements BaseAdapter.OnItemLongClickListener,
        OnM3u8InfoListener, SniffingCallback {

    public static final String PATH = "path";
    public static final String CLASS_NAME = "className";
    public static final String MULTIPLE = "multiple";
    public static final String HAS_LOAD = "load";
    public static final String ISLIVE = "isLive";
    public static final String ISZERO = "isZero";
    public static final String REFERER = "referer";
    public static final String PAGE_START = "page_start";
    public static final String BANGUMIROOT = "bangumiroot";

    private String path;
    private String referer;
    private String className;
    private int multiple = 1;
    private String serializeKey;
    private boolean isLive = false;
    private boolean hasLoad = false;
    private boolean isZero = false;
    private boolean isBangumiRoot = false;
    private  IVideo mIVideo;
    private BaseAdapter mVideoAdapter;

    /**
     * @param path
     * @param className
     * @param multiple
     * @param pageStart
     * @param hasLoad
     * @param isLive
     * @param isZero
     * @param referer
     * @param isBangumiRoot
     * @return
     */
    public static Fragment newInstance(String path, String className, int multiple, int pageStart, boolean hasLoad, boolean isLive, boolean isZero, String referer, boolean isBangumiRoot) {
        Fragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putString(PATH, path);
        args.putInt(MULTIPLE, multiple);
        args.putInt(PAGE_START, pageStart);
        args.putBoolean(HAS_LOAD, hasLoad);
        args.putString(REFERER, referer);
        args.putBoolean(ISZERO, isZero);
        args.putString(CLASS_NAME, className);
        args.putBoolean(ISLIVE, isLive);
        args.putBoolean(BANGUMIROOT, isBangumiRoot);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param path
     * @param className
     * @param multiple
     * @param hasLoad
     * @param isLive
     * @param isZero
     * @param referer
     * @param isBangumiRoot
     * @return
     */
    public static Fragment newInstance(String path, String className, int multiple, boolean hasLoad, boolean isLive, boolean isZero, String referer, boolean isBangumiRoot) {
        return newInstance(path, className, multiple, 1, hasLoad, isLive, isZero, referer, isBangumiRoot);
    }

    public static Fragment newInstance(String path, String className, int multiple, boolean hasLoad, boolean isLive, boolean isZero) {
        return newInstance(path, className, multiple, 1, hasLoad, isLive, isZero, null, false);
    }

    /**
     * @param path
     * @param className
     * @return
     */
    public static Fragment newInstance(String path, String className) {
        return newInstance(path, className, 1, false, false, false);
    }

    /**
     * @param path
     * @param className
     * @param multiple
     * @return
     */
    public static Fragment newInstance(String path, String className, int multiple) {
        return newInstance(path, className, multiple, true, false, false);
    }

    @Override
    protected boolean hasLoad() {
        return hasLoad;
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        path = getArguments().getString(PATH);
        className = getArguments().getString(CLASS_NAME);
        multiple = getArguments().getInt(MULTIPLE);
        hasLoad = getArguments().getBoolean(HAS_LOAD);
        isZero = getArguments().getBoolean(ISZERO);
        isLive = getArguments().getBoolean(ISLIVE, false);
        referer = getArguments().getString(REFERER);
        isBangumiRoot = getArguments().getBoolean(BANGUMIROOT);
        setPageStart(getArguments().getInt(PAGE_START, 1));
        serializeKey = getClass().getSimpleName() + "_" + path + "_" + className;
        super.initFragment(savedInstanceState, args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        M3u8Manager.INSTANCE.registerInfoListeners(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        M3u8Manager.INSTANCE.unregisterInfoListeners(this);
        SniffingUtil.get().releaseAll();
    }

    @Override
    protected void setListener() {
        super.setListener();
        if(mVideoAdapter != null) mVideoAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.GridLayoutManagerWrapper(activity, 3);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mVideoAdapter instanceof VideoIndexAdapter) {
            LoopViewPager loopViewPager = ((VideoIndexAdapter) mVideoAdapter).getLoopViewPager();
            if (loopViewPager != null && isVisibleToUser && !loopViewPager.isLoop() && loopViewPager.hasLoop()) {
                //用户可见的时候开启滚屏循环
                loopViewPager.startLoop();
            } else if (loopViewPager != null && !isVisibleToUser) {
                //不可见关闭
                loopViewPager.stopLoop();
            }
        }
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        if (!TextUtils.isEmpty(referer)) {
            picasso = new Picasso.Builder(activity).listener(new PicassoListener()).downloader(new RefererDownloader(activity, referer)).build();
        }
        return mVideoAdapter = isBangumiRoot ? new VideoIndexAdapter(activity, picasso) : new VideoListAdapter(activity, picasso, hasLoad);
    }

    @Override
    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
        enqueue(retrofit, page);
    }

    @Override
    public void loadLocalData(AsyTaskQueue queue) {
        queue.execute(taskListener);
    }

    @Override
    protected boolean useLocalStorage() {
        return true;
    }

    @Override
    public String getSerializeKey() {
        return serializeKey;
    }

    @Override
    public int getStaleTime() {
        return isLive ? 2 : 24;
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IVideo)) return;
        IVideo video = (IVideo) datas.get(position);
        if (isLive) {//直播
            LivePlayerActivity.startActivity(activity, video);
        } else {
            if (video.hasVideoDetails()) {//有视频详情页
                VideoDetailsActivity.startActivity(activity, video);
            } else {//没有视频详情页，直接跳转播放
                VideoPlayerActivity.startActivity(activity, video);
            }
        }
        LogUtil.e("onItemClick","====>" + new Gson().toJson(video));
    }

    @Override
    public boolean onItemLongClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IVideo)) return true;
        mIVideo = (IVideo) datas.get(position);
        if (mIVideo.hasVideoDetails()) {
            DialogUtil.showOperationDialog(this, mIVideo, (List<IVideo>) datas, position);
        } else if (!isLive) {
            DialogUtil.showDownloadOperationDialog(this, mIVideo, (List<IVideo>) datas, position, new DownloadCallback(mIVideo));
        }
        return true;
    }

    @Override
    public void onError(@NotNull M3u8File m3u8File, @NotNull Throwable throwable) {
        DialogUtil.closeProgressDialog();
    }

    @Override
    public void onSuccess(@NotNull M3u8File m3u8File, @NotNull List<M3u8> list) {
        if(mIVideo != null){
            showSnackbar("添加<" + mIVideo.getTitle() + ">成功");
        }
        M3u8Manager.INSTANCE.download(list);
        DialogUtil.closeProgressDialog();
    }

    @Override
    public void onSniffingSuccess(View webView, String webUrl, List<SniffingVideo> videos) {
        if (videos.isEmpty() || mIVideo == null) return;
        String url = videos.get(0).getUrl();
        String format = String.format("%s.mp4", mIVideo.getTitle());
        if (url.contains(".m3u")) {
            M3u8File m3u8File = new M3u8File();
            if(url.contains("=") && url.split("=")[1].contains(".m3u")){
                m3u8File.setUrl(url.split("=")[1]);
            }else{
                m3u8File.setUrl(url);
            }
            m3u8File.setM3u8VideoName(format);
            M3u8Manager.INSTANCE.download(m3u8File);
        } else if (url.contains(".rm") || url.contains(".mp4") || url.contains(".avi") || url.contains(".wmv")) {
            String path = new File(M3u8Config.INSTANCE.getM3u8Path(), format).getAbsolutePath();
            Map<String, String> header = AppUtil.getDownloadHeader();
            getDownloadReceiver().load(url).setExtendField(url).setFilePath(path).addHeaders(header).start();
            showToast(String.format("<%s>添加下载任务成功", format));
            DialogUtil.closeProgressDialog();
        } else {
            showToast(String.format("<%s>不支持下载", format));
            DialogUtil.closeProgressDialog();
        }
    }

    @Override
    public void onSniffingError(View webView, String url, int errorCode) {
        if(mIVideo != null){
            showSnackbar("添加<" + mIVideo.getTitle() + ">失败");
        }
        DialogUtil.closeProgressDialog();
    }

    /**
     * @param retrofit
     * @param page
     */
    private void enqueue(RetrofitManager retrofit, int page) {
        if (hasLoad) {
            Integer integer = page;
            if (page == 1 && isZero) {
                integer = 0;
            } else {
                integer = page == 1 ? 1 : multiple > 1 ? Integer.valueOf((page - 1) * multiple + 1) : Integer.valueOf(page * multiple);
            }
            if (!path.startsWith("http") && (path.contains("/") || path.split("/").length > 1)) {
                String[] split = path.split("/");
                if (split.length == 2) {
                    retrofit.enqueue(className, callback, "home", split[0], split[1], integer);
                } else if (split.length == 3) {
                    retrofit.enqueue(className, callback, "home", split[0], split[1], split[2], integer);
                }
            } else {
                retrofit.enqueue(className, callback, "home", path, integer);
            }
        } else {
            if (!path.startsWith("http") && (path.contains("/") || path.split("/").length > 1)) {
                String[] split = path.split("/");
                if (split.length == 2) {
                    retrofit.enqueue(className, callback, "home", split[0], split[1]);
                } else if (split.length == 3) {
                    retrofit.enqueue(className, callback, "home", split[0], split[1], split[2]);
                }
            } else {
                retrofit.enqueue(className, callback, "home", path);
            }
        }
    }

    private QueryTaskListener<IHomeRoot> taskListener = new QueryTaskListener<IHomeRoot>() {

        @Override
        public void onSuccess(IHomeRoot date) {
            if (!date.isSuccess() || mVideoAdapter == null) return;
            List<? extends IViewType> adapterResult = date.getAdapterResult();
            if (adapterResult == null || adapterResult.size() == 0) {
                mVideoAdapter.setLoad(false);
                showSnackbar(getStringFix(R.string.not_more));
            } else {
                mVideoAdapter.setLoad(true);
                mVideoAdapter.addData(date,true);
            }
        }

    };

    private RefreshRecyclerFragmentImpl<IHomeRoot> callback = new RefreshRecyclerFragmentImpl<IHomeRoot>() {

        @Override
        public void onSuccess(IHomeRoot response,boolean refresh) {
            if (!response.isSuccess() || mVideoAdapter == null) return;
            List<? extends IViewType> adapterResult = response.getAdapterResult();
            if (adapterResult == null || adapterResult.size() == 0) {
                mVideoAdapter.setLoad(false);
                showSnackbar(getStringFix(R.string.not_more));
            } else {
                mVideoAdapter.setLoad(hasLoad);
                mVideoAdapter.addData(response,refresh);
            }
        }

    };


    private class DownloadCallback implements RefreshCallback<IPlayUrls> {

        private IVideo mVideo;

        public DownloadCallback(IVideo mVideo){
            this.mVideo = mVideo;
        }

        @Override
        public void onStart(int enqueueKey) {
            if (activity == null) return;
            DialogUtil.showProgressDialog(activity, "正在获取下载地址...");
        }

        @Override
        public void onFinish(int enqueueKey) {
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            showToast(throwable);
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onSuccess(int enqueueKey, IPlayUrls response) {
            if (response == null || activity == null) return;
            if (response.isSuccess() && response.getUrls() != null && !response.getUrls().isEmpty()) {
                final String value = response.getMainUrl();
                if (response.getPlayType() == IVideoEpisode.PLAY_TYPE_VIDEO) {
                    String videoPath = AppUtil.getVideoPath(activity);
                    if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(videoPath)) {
                        if ((value.startsWith("http") || value.startsWith("ftp")) && !getDownloadReceiver().load(value).taskExists()) {
                            getDownloadReceiver().load(value).setDownloadPath(new File(videoPath, "video_" + System.currentTimeMillis() + ".mp4").getAbsolutePath()).start();
                            showSnackbar("添加<" + value + ">任务成功");
                        } else {
                            showSnackbar(getStringFix(R.string.task_exists));
                        }
                    } else {
                        showSnackbar(getStringFix(R.string.task_exists));
                    }
                    DialogUtil.closeProgressDialog();
                }else if(response.getPlayType() == IVideoEpisode.PLAY_TYPE_VIDEO_M3U8){
                    M3u8File m3u8File = new M3u8File();
                    if(value != null && value.contains("=")){
                        String[] split = value.split("=");
                        if(split.length == 2 && split[1].contains(".m3u")){
                            m3u8File.setUrl(split[1]);
                        }else{
                            String uReplace = split[0] + "=";
                            String replace = value.replace(uReplace, "");
                            if(replace.contains(".m3u")){
                                m3u8File.setUrl(replace);
                            }else{
                                m3u8File.setUrl(value);
                            }
                        }
                    }else if(value != null){
                        m3u8File.setUrl(value);
                    }
                    m3u8File.setM3u8VideoName(String.format("%s.mp4", mVideo.getTitle()));
                    M3u8Manager.INSTANCE.download(m3u8File);
                }else if(response.getPlayType() == IVideoEpisode.PLAY_TYPE_WEB || response.getPlayType() == IVideoEpisode.PLAY_TYPE_VIDEO_WEB){
                    SniffingUtil.get().activity(activity).url(value).referer(response.getReferer()).callback(VideoListFragment.this).start();
                } else {
                    showSnackbar(getStringFix(R.string.error_download_type));
                    DialogUtil.closeProgressDialog();
                }
            } else {
                showToast(getStringFix(R.string.error_play_conn));
                DialogUtil.closeProgressDialog();
            }
        }

    }

}
