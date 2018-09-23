package com.fanchen.imovie.dialog;

import android.content.DialogInterface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.EpisodeAdapter;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.service.Dm5Service;
import com.fanchen.imovie.util.DialogUtil;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DownloadDialog
 * Created by fanchen on 2017/10/8.
 */
public class DownloadDialog extends BottomBaseDialog<DownloadDialog> implements
        BaseAdapter.OnItemClickListener, View.OnClickListener, DialogInterface.OnDismissListener {

    private RecyclerView mRecyclerView;
    private Button mDownButton;

    private IVideoEpisode mDownload;
    private List<IVideoEpisode> mDownloads;
    private List<DownloadTemp> mDownloadTemps = new ArrayList<>();
    private BaseActivity activity;
    private EpisodeAdapter mEpisodeAdapter;
    private SoftReference<OnDownloadSelectListener> onDownloadSelectListener;

    public DownloadDialog(BaseActivity activity, IVideoDetails mVideoDetails, OnDownloadSelectListener onDownloadSelectListener) {
        super(activity);
        this.activity = activity;
        this.onDownloadSelectListener = new SoftReference<>(onDownloadSelectListener);
        mEpisodeAdapter = new EpisodeAdapter(activity, false, true);
        mEpisodeAdapter.addAll(mVideoDetails.getEpisodes());
    }

    @Override
    public View onCreateView() {
        View inflate = View.inflate(getContext(), R.layout.dialog_download, null);
        mRecyclerView = (RecyclerView) inflate.findViewById(R.id.rv_download_list);
        mRecyclerView.setLayoutManager(new BaseAdapter.GridLayoutManagerWrapper(getContext(), 3));
        mRecyclerView.setAdapter(mEpisodeAdapter);
        mDownButton = (Button) inflate.findViewById(R.id.bt_download_selete);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        mDownButton.setOnClickListener(this);
        mEpisodeAdapter.setOnItemClickListener(this);
        setOnDismissListener(this);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IVideoEpisode)) return;
        IVideoEpisode videoEpisode = (IVideoEpisode) datas.get(position);
        if (!videoEpisode.getUrl().contains("http")) {
            activity.showToast("该视频不支持下载");
        } else if (mEpisodeAdapter.getSelect().size() >= 3 && videoEpisode.getDownloadState() == IVideoEpisode.DOWNLOAD_NON) {
            activity.showToast("一次最多支持选中下载3个");
        } else {
            if (videoEpisode.getDownloadState() == IVideoEpisode.DOWNLOAD_SELECT) {
                videoEpisode.setDownloadState(IVideoEpisode.DOWNLOAD_NON);
            } else if (videoEpisode.getDownloadState() == IVideoEpisode.DOWNLOAD_NON) {
                videoEpisode.setDownloadState(IVideoEpisode.DOWNLOAD_SELECT);
            }
            mEpisodeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (mEpisodeAdapter == null) return;
        mDownloads = mEpisodeAdapter.getSelect();
        if (mDownloads == null || mDownloads.size() == 0) {
            activity.showToast(R.string.error_download_non);
        } else {
            DialogUtil.showProgressDialog(activity, activity.getString(R.string.download_ing));
            download(mDownloads.remove(0));
        }
    }

    private void download(IVideoEpisode select) {
        this.mDownload = select;
        if (this.mDownload == null) return;
        if (IVideoEpisode.PLAY_TYPE_VIDEO == select.getPlayerType() && !TextUtils.isEmpty(select.getUrl())) {//可以直接下载
            DownloadTemp temp = new DownloadTemp(mDownload, DownloadTemp.TYPE_MP4);
            temp.url = mDownload.getUrl();
            mDownloadTemps.add(temp);
            callback.onFinish(-1);
        } else if (IVideoEpisode.PLAY_TYPE_VIDEO_M3U8 == select.getPlayerType()) {//M3u8
            DownloadTemp temp = new DownloadTemp(mDownload, DownloadTemp.TYPE_M3U8);
            temp.m3u8Url = mDownload.getUrl();
            mDownloadTemps.add(temp);
        } else if (IVideoEpisode.PLAY_TYPE_URL == select.getPlayerType()) {//需要去解析
            String className = select.getServiceClassName();
            RetrofitManager with = RetrofitManager.with(activity.getApplicationContext());
            if (Dm5Service.class.getName().equals(className)) {
                String[] split = select.getId().split("\\?");
                with.enqueue(className, callback, "playUrl", split[0], split[1].replace("link=", ""));
            } else {
                with.enqueue(className, callback, "playUrl", select.getId());
            }
        } else {
            activity.showToast(mDownload.getTitle() + "不支持下载");
        }
    }

    private RefreshCallback<IPlayUrls> callback = new RefreshCallback.RefreshCallbackImpl<IPlayUrls>() {

        @Override
        public void onFinish(int enqueueKey) {
            if (mDownloads != null && mDownloads.size() > 0) {
                download(mDownloads.remove(0));
            } else if (onDownloadSelectListener != null) {
                DialogUtil.closeProgressDialog();
                OnDownloadSelectListener listener = onDownloadSelectListener.get();
                if (listener != null && mDownloadTemps != null) {
                    listener.onDownloadSelect(mDownloadTemps);
                }
                DownloadDialog.this.dismiss();
            }
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            activity.showToast(mDownload.getTitle() + "下载失败");
        }

        @Override
        public void onSuccess(int enqueueKey, IPlayUrls response) {
            if (mDownload != null && isEmpty(response)) {
                activity.showToast(mDownload.getTitle() + "下载失败");
            } else if (mDownload != null) {
                String value = response.getUrls().entrySet().iterator().next().getValue();
                if (value.contains("=") && value.contains(".m3u")) value = value.split("=")[1];
                if(value.startsWith("ftp://") || value.startsWith("xg://")){
                    DownloadTemp temp = new DownloadTemp(mDownload, DownloadTemp.TYPE_XIGUA);
                    temp.referer = response.getReferer();
                    temp.url = value;
                    mDownloadTemps.add(temp);
                }else if (response.getUrlType() == IPlayUrls.URL_FILE) {
                    DownloadTemp temp = new DownloadTemp(mDownload, DownloadTemp.TYPE_MP4);
                    temp.referer = response.getReferer();
                    temp.url = value;
                    mDownloadTemps.add(temp);
                } else if (response.getPlayType() == IPlayUrls.URL_M3U8 || value.contains(".m3u")) {
                    DownloadTemp temp = new DownloadTemp(mDownload, DownloadTemp.TYPE_M3U8);
                    temp.referer = response.getReferer();
                    temp.m3u8Url = value;
                    mDownloadTemps.add(temp);
                } else if (response.getPlayType() == IPlayUrls.URL_WEB) {
                    DownloadTemp temp = new DownloadTemp(mDownload, DownloadTemp.TYPE_URL);
                    temp.referer = response.getReferer();
                    temp.url = value;
                    mDownloadTemps.add(temp);
                } else {
                    activity.showToast(mDownload.getTitle() + "不支持下载");
                }
            }
        }

        private boolean isEmpty(IPlayUrls iPlayUrls) {
            if (iPlayUrls == null) return true;
            Map<String, String> urls = iPlayUrls.getUrls();
            if (urls == null || urls.isEmpty()) return true;
            Map.Entry<String, String> next = urls.entrySet().iterator().next();
            if (TextUtils.isEmpty(next.getValue())) return true;
            return false;
        }

    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mEpisodeAdapter != null) {
            mEpisodeAdapter.resetDownloadState();
        }
        if (activity instanceof OnDismissListener) {
            ((OnDismissListener) activity).onDismiss(dialog);
        }
    }

    public static class DownloadTemp {
        public static final int TYPE_M3U8 = 1;
        public static final int TYPE_MP4 = 2;
        public static final int TYPE_URL = 3;
        public static final int TYPE_XIGUA = 4;
        public IVideoEpisode episode;
        public String m3u8Url = "";
        public String url = "";
        public String referer = "";
        public int type = TYPE_M3U8;

        public DownloadTemp(IVideoEpisode episode, int type) {
            this.episode = episode;
            this.type = type;
        }

        public String getOnlyId(){
            return String.format("%s__%s",episode.getUrl(),episode.getId());
        }
    }

    public interface OnDownloadSelectListener {

        void onDownloadSelect(List<DownloadTemp> downloads);

    }

}
