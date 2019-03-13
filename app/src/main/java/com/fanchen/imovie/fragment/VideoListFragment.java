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
import com.fanchen.imovie.picasso.download.RefererDownloader;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.callback.RetrofitCallback;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.view.pager.LoopViewPager;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

/**
 * VideoListFragment
 * Created by fanchen on 2017/9/23.
 */
public class VideoListFragment extends BaseRecyclerFragment implements BaseAdapter.OnItemLongClickListener {

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
        if (mVideoAdapter != null && mVideoAdapter instanceof VideoIndexAdapter) {
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
            picasso = new Picasso.Builder(activity).downloader(new RefererDownloader(activity, referer)).build();
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
    }

    @Override
    public boolean onItemLongClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IVideo)) return true;
        IVideo video = (IVideo) datas.get(position);
        if (video.hasVideoDetails()) {
            DialogUtil.showOperationDialog(this, video, (List<IVideo>) datas, position);
        } else if (!isLive) {
            DialogUtil.showDownloadOperationDialog(this, video, (List<IVideo>) datas, position, downloadCallback);
        }
        return true;
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
                mVideoAdapter.addData(date);
            }
        }

    };

    private RefreshRecyclerFragmentImpl<IHomeRoot> callback = new RefreshRecyclerFragmentImpl<IHomeRoot>() {

        @Override
        public void onSuccess(IHomeRoot response) {
            if (!response.isSuccess() || mVideoAdapter == null) return;
            List<? extends IViewType> adapterResult = response.getAdapterResult();
            if (adapterResult == null || adapterResult.size() == 0) {
                mVideoAdapter.setLoad(false);
                showSnackbar(getStringFix(R.string.not_more));
            } else {
                mVideoAdapter.setLoad(hasLoad);
                mVideoAdapter.addData(response);
            }
        }

    };

    private RetrofitCallback<IPlayUrls> downloadCallback = new RefreshCallback<IPlayUrls>() {

        @Override
        public void onStart(int enqueueKey) {
            if (activity == null) return;
            DialogUtil.showProgressDialog(activity, "正在获取下载地址...");
        }

        @Override
        public void onFinish(int enqueueKey) {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            showToast(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, IPlayUrls response) {
            if (response == null || activity == null) return;
            if (response.isSuccess() && response.getUrls() != null && !response.getUrls().isEmpty()) {
                final String value = response.getUrls().entrySet().iterator().next().getValue();
                if (response.getPlayType() == IVideoEpisode.PLAY_TYPE_VIDEO) {
                    String videoPath = AppUtil.getVideoPath(activity);
                    if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(videoPath)) {
                        if ((value.startsWith("http") || value.startsWith("ftp")) && !getDownloadReceiver().load(value).taskExists()) {
                            getDownloadReceiver().load(value).setDownloadPath(new File(videoPath, "video_" + System.currentTimeMillis() + ".mp4").getAbsolutePath()).start();
                        } else {
                            showSnackbar(getStringFix(R.string.task_exists));
                        }
                    } else {
                        showSnackbar(getStringFix(R.string.task_exists));
                    }
                } else {
                    showSnackbar(getStringFix(R.string.error_download_type));
                }
            } else {
                showToast(getStringFix(R.string.error_play_conn));
            }
        }

    };
}
