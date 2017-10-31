package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.arialyy.aria.core.Aria;
import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.adapter.VideoListAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.callback.RetrofitCallback;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.util.DialogUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by fanchen on 2017/9/23.
 */
public class VideoListFragment extends BaseRecyclerFragment implements BaseAdapter.OnItemLongClickListener {

    public static final String PATH = "path";
    public static final String CLASS_NAME = "className";
    public static final String MULTIPLE = "multiple";
    public static final String HAS_LOAD = "load";
    public static final String ISLIVE = "isLive";
    public static final String ISZERO = "isZero";

    private String path;
    private String className;
    private int multiple = 1;
    private String serializeKey;
    private boolean isLive = false;
    private boolean hasLoad = false;
    private boolean isZero = false;
    private VideoListAdapter mVideoListAdapter;

    /**
     * @param path
     * @param className
     * @param multiple
     * @return
     */
    public static Fragment newInstance(String path, String className, int multiple, boolean hasLoad, boolean isLive,boolean isZero) {
        Fragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putString(PATH, path);
        args.putInt(MULTIPLE, multiple);
        args.putBoolean(HAS_LOAD, hasLoad);
        args.putBoolean(ISZERO, isZero);
        args.putString(CLASS_NAME, className);
        args.putBoolean(ISLIVE, isLive);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param path
     * @param className
     * @return
     */
    public static Fragment newInstance(String path, String className) {
        return newInstance(path, className, 1, false, false,false);
    }

    /**
     * @param path
     * @param className
     * @param multiple
     * @return
     */
    public static Fragment newInstance(String path, String className, int multiple) {
        return newInstance(path, className, multiple, true, false,false);
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
        serializeKey = getClass().getSimpleName() + "_" + path + "_" + className;
        super.initFragment(savedInstanceState, args);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mVideoListAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(activity, 3);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mVideoListAdapter = new VideoListAdapter(activity, picasso, hasLoad);
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
        if (datas == null || datas.size() <= position || !(datas.get(position) instanceof IVideo)) return;
        IVideo video = (IVideo) datas.get(position);
        if (video.hasVideoDetails()) {
            VideoDetailsActivity.startActivity(activity, video);
        } else {
            VideoPlayerActivity.startActivity(activity, video, isLive);
        }
    }

    @Override
    public boolean onItemLongClick(List<?> datas, View v, int position) {
        if (datas == null || datas.size() <= position || !(datas.get(position) instanceof IVideo)) return true;
        IVideo video = (IVideo) datas.get(position);
        if (video.hasVideoDetails()) {
            DialogUtil.showOperationDialog(this, video,(List<IVideo>)datas,position);
        } else if(!isLive){
            DialogUtil.showDownloadOperationDialog(this, video,(List<IVideo>)datas,position,downloadCallback);
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
            if(page == 1 && isZero){
                integer = 0;
            }else{
                integer = page == 1 ? 1: multiple > 1 ?Integer.valueOf((page-1) * multiple + 1) :Integer.valueOf(page * multiple);
            }
            retrofit.enqueue(className, callback, "home", path, integer);
        } else {
            retrofit.enqueue(className, callback, "home", path);
        }
    }

    private QueryTaskListener<IHomeRoot> taskListener = new QueryTaskListener<IHomeRoot>() {

        @Override
        public void onSuccess(IHomeRoot date) {
            if (!date.isSuccess()) return;
            List<? extends IViewType> adapterResult = date.getAdapterResult();
            if (adapterResult == null || adapterResult.size() == 0) {
                mVideoListAdapter.setLoad(false);
                showSnackbar(getString(R.string.not_more));
            } else {
                mVideoListAdapter.setLoad(hasLoad);
                mVideoListAdapter.addAll(adapterResult);
            }
        }

    };

    private RefreshRecyclerFragmentImpl<IHomeRoot> callback = new RefreshRecyclerFragmentImpl<IHomeRoot>() {

//        @Override
//        public void onSuccess(int enqueueKey, IHomeRoot response) {
//            if (!isAdded() || response == null || !response.isSuccess()) return;
//            AsyTaskQueue.newInstance().execute(new SaveTaskListener(response));
//            if (isRefresh()) mVideoListAdapter.clear();
//            List<? extends IViewType> adapterResult = response.getAdapterResult();
//            if (adapterResult == null || adapterResult.size() == 0) {
//                mVideoListAdapter.setLoad(false);
//                showSnackbar(getString(R.string.not_more));
//            } else {
//                mVideoListAdapter.setLoad(hasLoad);
//                mVideoListAdapter.addAll(adapterResult);
//            }
//        }

        @Override
        public void onSuccess(IHomeRoot response) {
            if (!response.isSuccess()) return;
            List<? extends IViewType> adapterResult = response.getAdapterResult();
            if (adapterResult == null || adapterResult.size() == 0) {
                mVideoListAdapter.setLoad(false);
                showSnackbar(getString(R.string.not_more));
            } else {
                mVideoListAdapter.setLoad(hasLoad);
                mVideoListAdapter.addAll(adapterResult);
            }
        }

    };

    private RetrofitCallback<IPlayUrls> downloadCallback = new RefreshCallback<IPlayUrls>() {

        @Override
        public void onStart(int enqueueKey) {
            if (isDetached() || !isAdded()) return;
            DialogUtil.showProgressDialog(activity, "正在获取下载地址...");
        }

        @Override
        public void onFinish(int enqueueKey) {
            if (isDetached() || !isAdded()) return;
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            if (isDetached() || !isAdded()) return;
            showToast(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, IPlayUrls response) {
            if (isDetached() || !isAdded() || response == null) return;
            if (response.isSuccess() && response.getUrls() != null && !response.getUrls().isEmpty()) {
                final String value = response.getUrls().entrySet().iterator().next().getValue();
                if (response.getPlayType() == IVideoEpisode.PLAY_TYPE_VIDEO) {
                    if (!Aria.download(activity.appliction).load(value).taskExists()) {
                        File dir = new File(Environment.getExternalStorageDirectory() + "/android/data/" + activity.getPackageName() + "/download/video/");
                        if (!dir.exists())
                            dir.mkdirs();
                        Aria.download(activity.appliction).load(value).setDownloadPath(new File(dir, "video_" + System.currentTimeMillis() + ".mp4").getAbsolutePath()).start();
                    }else{
                        showSnackbar(getString(R.string.task_exists));
                    }
                }else{
                    showSnackbar(getString(R.string.error_download_type));
                }
            } else {
                showToast(getString(R.string.error_play_conn));
            }
        }

    };
}
