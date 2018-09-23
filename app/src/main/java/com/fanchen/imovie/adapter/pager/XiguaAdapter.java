package com.fanchen.imovie.adapter.pager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.XiguaDownload;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.util.AppUtil;
import com.xigua.p2p.TaskVideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * XiguaAdapter
 * Created by fanchen on 2018/9/21.
 */
public class XiguaAdapter extends BaseAdapter implements View.OnClickListener {

    private boolean isDeleteMode;
    private OnXiguaControlListener onXiguaControlListener;

    public XiguaAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new XiguaViewHolder(v);
    }

    /**
     * @param all
     */
    public void setTaskVideoInfos(List<TaskVideoInfo> all) {
        List<IViewType> iViewTypes = new ArrayList<>();
        for (TaskVideoInfo info : all) {
            iViewTypes.add(new XiguaDownload(info));
        }
        super.clear();
        super.addAll(iViewTypes);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position) return;
        XiguaDownload download = (XiguaDownload) datas.get(position);
        XiguaViewHolder viewHolder = (XiguaViewHolder) holder;
        int totalSize = (int) download.taskVideoInfo.getTotalSize();
        int downSize = (int) download.taskVideoInfo.getDownSize();
        int localSize = (int) download.taskVideoInfo.getLocalSize();
        int speed = (int) download.taskVideoInfo.getSpeed();
        viewHolder.downloadName.setText(download.getFileName());
        viewHolder.progressBar.setMax(totalSize);
        viewHolder.progressBar.setProgress(downSize);
        viewHolder.downloadLength.setText(AppUtil.getSize(totalSize));
        if (localSize > 0) {
            viewHolder.progressText.setText("下载成功");
            viewHolder.progressBar.setMax(100);
            viewHolder.progressBar.setProgress(100);
            viewHolder.downloadLength.setText(AppUtil.getSize(localSize));
        } else if (download.taskVideoInfo.getState() == TaskVideoInfo.START) {
            viewHolder.progressText.setText("正在下载");
            viewHolder.downloadLength.setText(String.format("%s/s", AppUtil.getSize(speed)));
        } else if (download.taskVideoInfo.getState() == TaskVideoInfo.PAUSE) {
            viewHolder.progressText.setText("暂停下载");
        } else {
            viewHolder.progressText.setText("未知状态");
        }
        viewHolder.btnDelete.setTag(download.taskVideoInfo);
        viewHolder.btnDelete.setOnClickListener(this);
        viewHolder.downloadControl.setTag(download.taskVideoInfo);
        viewHolder.downloadControl.setOnClickListener(this);
        viewHolder.btnPlay.setTag(download.taskVideoInfo);
        viewHolder.btnPlay.setOnClickListener(this);
        viewHolder.btnDelete.setVisibility(View.GONE);
        setBtnState(viewHolder, download.taskVideoInfo);
        if (!isDeleteMode) return;
        viewHolder.btnPlay.setVisibility(View.GONE);
        viewHolder.downloadControl.setVisibility(View.GONE);
        viewHolder.btnDelete.setVisibility(View.VISIBLE);
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_download_row;
    }

    /**
     * @param isDeleteMode
     */
    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        notifyDataSetChanged();
    }

    public void setOnXiguaControlListener(OnXiguaControlListener onXiguaControlListener) {
        this.onXiguaControlListener = onXiguaControlListener;
    }

    private void setBtnState(XiguaViewHolder viewHolder, TaskVideoInfo entity) {
        if (entity.getLocalSize() > 0) {
            viewHolder.progressBar.setProgress(100);
            viewHolder.btnPlay.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setVisibility(View.GONE);
        } else if (entity.getState() == TaskVideoInfo.START) {
            viewHolder.btnPlay.setVisibility(View.GONE);
            viewHolder.downloadControl.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_pause);
        } else if (entity.getState() == TaskVideoInfo.PAUSE) {
            viewHolder.btnPlay.setVisibility(View.GONE);
            viewHolder.downloadControl.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_start);
        } else {
            viewHolder.btnPlay.setVisibility(View.GONE);
            viewHolder.downloadControl.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_start);
        }
    }

    @Override
    public void onClick(View v) {
        if (onXiguaControlListener == null) return;
        TaskVideoInfo info = (TaskVideoInfo) v.getTag();
        switch (v.getId()) {
            case R.id.downloadControl:
                if (info.getState() == TaskVideoInfo.START) {
                    onXiguaControlListener.onControl(this, OnXiguaControlListener.STOP, info);
                } else {
                    onXiguaControlListener.onControl(this, OnXiguaControlListener.START, info);
                }
                break;
            case R.id.btnDelete:
                onXiguaControlListener.onControl(this, OnXiguaControlListener.DELETE, info);
                break;
            case R.id.btnPlay:
                onXiguaControlListener.onControl(this, OnXiguaControlListener.PLAY, info);
                break;
        }
    }

    private static class XiguaViewHolder extends RecyclerView.ViewHolder {
        public TextView downloadName;//   文件名,如xxxx第几集
        public TextView downloadLength;//  大小：15M/88M
        public TextView progressText;// 网速/下载中/暂停中/等待中/
        public ProgressBar progressBar;//进度条
        public ImageButton downloadControl;// 最右边的控制按钮:下载/暂停
        public ImageButton btnDelete;// 删除按钮
        public ImageButton btnPlay;// 播放按钮

        public XiguaViewHolder(View itemView) {
            super(itemView);
            downloadName = (TextView) itemView.findViewById(R.id.downloadName);
            downloadLength = (TextView) itemView.findViewById(R.id.downloadLength);
            progressText = (TextView) itemView.findViewById(R.id.rowProgress);
            progressBar = (ProgressBar) itemView.findViewById(R.id.ProgressBar);
            downloadControl = (ImageButton) itemView.findViewById(R.id.downloadControl);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
            btnPlay = (ImageButton) itemView.findViewById(R.id.btnPlay);
        }
    }

    public interface OnXiguaControlListener {

        int DELETE = 0;
        int PLAY = 1;
        int START = 2;
        int STOP = 3;

        void onControl(BaseAdapter adapter, int control, TaskVideoInfo info);

    }
}
