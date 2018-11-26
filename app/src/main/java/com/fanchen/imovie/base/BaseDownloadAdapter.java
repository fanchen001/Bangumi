package com.fanchen.imovie.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.util.LogUtil;

import java.util.List;

/**
 * BaseDownloadAdapter
 * Created by fanchen on 2018/10/18.
 */
public abstract class BaseDownloadAdapter<T extends BaseDownloadAdapter.DownloadWarp<?>>
        extends BaseAdapter implements View.OnClickListener {

    protected boolean isDeleteMode;
    protected BaseActivity baseActivity;
    private OnDownloadControlListener<T> onDownloadControlListener;

    public BaseDownloadAdapter(BaseActivity context) {
        super(context);
        this.baseActivity = context;
    }

    public BaseDownloadAdapter(BaseActivity context, List<IViewType> mList) {
        super(context, mList);
        this.baseActivity = context;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new DownloadViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position) return;
        T data = (T) datas.get(position);
        data.position = position;
        DownloadViewHolder viewHolder = (DownloadViewHolder) holder;
        data.holder = viewHolder;
        viewHolder.btnDelete.setTag(data);
        viewHolder.btnDelete.setOnClickListener(this);
        viewHolder.btnDelete.setVisibility(View.GONE);
        viewHolder.downloadControl.setTag(data);
        viewHolder.downloadControl.setOnClickListener(this);
        viewHolder.btnPlay.setTag(data);
        viewHolder.btnPlay.setOnClickListener(this);
        viewHolder.downloadName.setTag(data);
        viewHolder.downloadName.setText(data.getName());
        viewHolder.downloadLength.setText("");
        setBtnState(viewHolder, data);
    }

    /**
     * @param onDownloadControlListener
     */
    public void setOnDownloadControlListener(OnDownloadControlListener<T> onDownloadControlListener) {
        this.onDownloadControlListener = onDownloadControlListener;
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

    @Override
    public void onClick(View v) {
        if (onDownloadControlListener == null || v.getTag() == null) return;
        T info = (T) v.getTag();
        if (v.getId() == R.id.downloadControl) {
            if (info.getDownloadState() != DownloadWarp.DOWNLOADING) {
                onDownloadControlListener.onControl(this, OnDownloadControlListener.START, info);
            } else {
                onDownloadControlListener.onControl(this, OnDownloadControlListener.STOP, info);
            }
        } else if (v.getId() == R.id.btnDelete) {
            onDownloadControlListener.onControl(this, OnDownloadControlListener.DELETE, info);
        } else if (v.getId() == R.id.btnPlay) {
            onDownloadControlListener.onControl(this, OnDownloadControlListener.PLAY, info);
        }
    }

    /**
     * @param viewHolder
     * @param entity
     */
    protected void setBtnState(DownloadViewHolder viewHolder, DownloadWarp entity) {
        viewHolder.progressText.setText(entity.getStateString());
        if (entity.getMax() >= Integer.MAX_VALUE) {
            viewHolder.progressBar.setMax((int) (entity.getMax() / 100));
            viewHolder.progressBar.setProgress((int) (entity.getProgress() / 100));
        } else {
            viewHolder.progressBar.setMax((int) entity.getMax());
            viewHolder.progressBar.setProgress((int) entity.getProgress());
        }
        viewHolder.downloadLength.setText(entity.getMessage());
        if (entity.getDownloadState() == DownloadWarp.SUCCESS) {
            viewHolder.btnPlay.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setVisibility(View.GONE);
        } else if (entity.getDownloadState() == DownloadWarp.DOWNLOADING) {
            viewHolder.btnPlay.setVisibility(View.GONE);
            viewHolder.downloadControl.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_pause);
        } else {
            viewHolder.btnPlay.setVisibility(View.GONE);
            viewHolder.downloadControl.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_start);
        }
        if (!isDeleteMode) return;
        viewHolder.btnPlay.setVisibility(View.GONE);
        viewHolder.downloadControl.setVisibility(View.GONE);
        viewHolder.btnDelete.setVisibility(View.VISIBLE);
    }

    public static class DownloadViewHolder extends RecyclerView.ViewHolder {
        public TextView downloadName;//   文件名,如xxxx第几集
        public TextView downloadLength;//  大小：15M/88M
        public TextView progressText;// 网速/下载中/暂停中/等待中/
        public ProgressBar progressBar;//进度条
        public ImageButton downloadControl;// 最右边的控制按钮:下载/暂停
        public ImageButton btnDelete;// 删除按钮
        public ImageButton btnPlay;// 播放按钮

        public DownloadViewHolder(View itemView) {
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

    public static abstract class DownloadWarp<T> implements IViewType {

        public static final int DOWNLOADING = 1;//正在下载
        public static final int ERROR = 2;//错误
        public static final int WAIT = 3;//等待。停止
        public static final int SUCCESS = 4;//下载成功
        public static final int STOP = 5;

        public DownloadViewHolder holder;
        public int position;
        public T data;

        /**
         * @return
         */
        public abstract String getMessage();

        /**
         * @return
         */
        public abstract String getName();

        /**
         * DownloadState
         *
         * @return
         */
        public abstract int getDownloadState();

        /**
         * Progress
         *
         * @return
         */
        public abstract long getProgress();

        /**
         * max Progress
         *
         * @return
         */
        public abstract long getMax();

        /**
         * format speed
         *
         * @param speed
         * @return
         */
        public String getSpeed(long speed) {
            if (data == null) return "";
            if (speed > 1024 * 1024) {
                return String.format("%.2fMb/s", speed / 1.0f / 1024 / 1024);
            } else if (speed > 1024) {
                return String.format("%.2fKb/s", speed / 1.0f / 1024);
            } else {
                return speed + "B/s";
            }
        }

        /**
         * format FileSize
         *
         * @param size
         * @return
         */
        public String getFileSize(long size) {
            if (data == null) return "";
            if (size > 1024 * 1024) {
                return String.format("%.2fMb", size / 1.0f / 1024 / 1024);
            } else if (size > 1024) {
                return String.format("%.2fKb", size / 1.0f / 1024);
            } else {
                return size + "B";
            }
        }

        @Override
        public int getViewType() {
            return TYPE_NORMAL;
        }

        public String getStateString() {
            int state = getDownloadState();
            if (state == SUCCESS) {
                return "完成";
            } else if (state == WAIT) {
                return "等待中";
            } else if (state == ERROR) {
                return "下载失败";
            } else if (state == STOP) {
                return "停止下载";
            }
            return "下载中";
        }

    }

    public interface OnDownloadControlListener<T> {
        int DELETE = 0;
        int PLAY = 1;
        int START = 2;
        int STOP = 3;

        void onControl(BaseAdapter adapter, int control, T data);
    }
}
