package com.fanchen.imovie.dialog;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.arialyy.aria.core.download.DownloadEntity;
import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.EpisodeAdapter;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.DialogUtil;

import java.util.List;

/**
 * Created by fanchen on 2017/10/8.
 */
public class DownloadDialog extends BottomBaseDialog<DownloadDialog> implements
        BaseAdapter.OnItemClickListener, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private Button mAllButton;
    private Button mDownButton;

    private BaseActivity activity;
    private IVideoDetails mVideoDetails;
    private IVideoEpisode mDownload;
    private List<IVideoEpisode> mDownloads;
    private EpisodeAdapter mEpisodeAdapter;
    private RetrofitManager mRetrofitManager;

    public DownloadDialog(BaseActivity activity, IVideoDetails mVideoDetails) {
        super(activity);
        this.activity = activity;
        this.mVideoDetails = mVideoDetails;
        mRetrofitManager = RetrofitManager.with(context);
        mEpisodeAdapter = new EpisodeAdapter(activity, false, true);
        mEpisodeAdapter.addAll(mVideoDetails.getEpisodes());
    }

    @Override
    public View onCreateView() {
        View inflate = View.inflate(getContext(), R.layout.dialog_download, null);
        mRecyclerView = (RecyclerView) inflate.findViewById(R.id.rv_download_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.setAdapter(mEpisodeAdapter);
        mAllButton = (Button) inflate.findViewById(R.id.bt_download_all);
        mDownButton = (Button) inflate.findViewById(R.id.bt_download_selete);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        mAllButton.setOnClickListener(this);
        mDownButton.setOnClickListener(this);
        mEpisodeAdapter.setOnItemClickListener(this);
        setOnDismissListener((OnDismissListener) activity);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IVideoEpisode)) return;
        IVideoEpisode videoEpisode = (IVideoEpisode) datas.get(position);
        if (videoEpisode.getDownloadState() == IVideoEpisode.DOWNLOAD_SELECT) {
            videoEpisode.setDownloadState(IVideoEpisode.DOWNLOAD_NON);
        } else if (videoEpisode.getDownloadState() == IVideoEpisode.DOWNLOAD_NON) {
            videoEpisode.setDownloadState(IVideoEpisode.DOWNLOAD_SELECT);
        }
        mEpisodeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (mEpisodeAdapter == null) return;
        switch (v.getId()) {
            case R.id.bt_download_all:
                mAllButton.setText(mEpisodeAdapter.isSelectAll() ? "取消全选" : "全部选中");
                mEpisodeAdapter.setSelectAll(!mEpisodeAdapter.isSelectAll());
                break;
            case R.id.bt_download_selete:
                mDownloads = mEpisodeAdapter.getSelect();
                if (mDownloads == null || mDownloads.size() == 0) {
                    activity.showToast(R.string.error_download_non);
                } else {
                    DialogUtil.showProgressDialog(activity, activity.getString(R.string.download_ing));
                    download(mDownloads.remove(0));
                }
                break;
        }
    }

    private void download(IVideoEpisode select) {
        this.mDownload = select;
        String videoPath = AppUtil.getVideoPath(context);
        if (TextUtils.isEmpty(videoPath) || this.mDownload == null) return;
        if (IVideoEpisode.PLAY_TYPE_VIDEO == select.getPlayerType() && !TextUtils.isEmpty(select.getUrl())) {
            String url = select.getUrl();
            if(url.startsWith("http") || url.startsWith("ftp")){
                String fileNmae = mVideoDetails.getTitle() + "_" + select.getTitle() + ".mp4";
                DownloadEntity downloadEntity = new DownloadEntity();
                downloadEntity.setUrl(url);
                downloadEntity.setMd5Code(url);
                downloadEntity.setFileName(fileNmae);
                downloadEntity.setDownloadPath(videoPath + fileNmae);
                activity.getDownloadReceiver().load(downloadEntity).start();
                mDownload.setDownloadState(IVideoEpisode.DOWNLOAD_RUN);
            }
            callback.onFinish(-1);
        } else if (IVideoEpisode.PLAY_TYPE_URL == select.getPlayerType()) {
            try {
                String[] split = select.getId().split("\\?");
                if (split.length == 2) {
                    mRetrofitManager.enqueue(select.getServiceClassName(), callback, "playUrl", split[0], split[1].replace("link=", ""));
                } else {
                    mRetrofitManager.enqueue(select.getServiceClassName(), callback, "playUrl", split[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DialogUtil.closeProgressDialog();
            activity.showToast(R.string.error_download_type);
        }
    }

    private RefreshCallback<IPlayUrls> callback = new RefreshCallback<IPlayUrls>() {

        @Override
        public void onStart(int enqueueKey) {
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
        }

        @Override
        public void onFinish(int enqueueKey) {
            if (mEpisodeAdapter != null)
                mEpisodeAdapter.notifyDataSetChanged();
            if (mDownloads != null && mDownloads.size() > 0) {
                download(mDownloads.remove(0));
            } else {
                if (mEpisodeAdapter != null) {
                    for (IVideoEpisode episode : (List<IVideoEpisode>) mEpisodeAdapter.getList()) {
                        if (episode.getDownloadState() == IVideoEpisode.DOWNLOAD_SELECT) {
                            episode.setDownloadState(IVideoEpisode.DOWNLOAD_NON);
                        }
                    }
                }
                dismiss();
                DialogUtil.closeProgressDialog();
            }
        }

        @Override
        public void onSuccess(int enqueueKey, IPlayUrls response) {
            String videoPath = AppUtil.getVideoPath(context);
            if (mDownload == null || mVideoDetails == null || response == null || TextUtils.isEmpty(videoPath))
                return;
            if (response != null && response.getUrls() != null && !response.getUrls().isEmpty() && !TextUtils.isEmpty(response.getUrls().entrySet().iterator().next().getValue())) {
                if (response.getUrlType() == IPlayUrls.URL_FILE) {
                    String value = response.getUrls().entrySet().iterator().next().getValue();
                    if(value.startsWith("http") || value.startsWith("ftp")){
                        String fileNmae = mVideoDetails.getTitle() + "_" + mDownload.getTitle() + ".mp4";
                        DownloadEntity downloadEntity = new DownloadEntity();
                        downloadEntity.setMd5Code(mDownload.getUrl());
                        downloadEntity.setUrl(value);
                        downloadEntity.setFileName(fileNmae);
                        downloadEntity.setDownloadPath(videoPath + "/" + fileNmae);
                        activity.getDownloadReceiver().load(downloadEntity).start();
                        mDownload.setDownloadState(IVideoEpisode.DOWNLOAD_RUN);
                        activity.showToast(mVideoDetails.getTitle() + "_" + mDownload.getTitle() + "添加下载成功");
                    }
                } else {
                    activity.showToast(mVideoDetails.getTitle() + "_" + mDownload.getTitle() + "下载失败");
                }
            }
        }

    };

}
