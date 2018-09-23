package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.M3u8Warp;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.m3u8.bean.M3u8;
import com.fanchen.m3u8.bean.M3u8File;
import com.fanchen.m3u8.bean.M3u8State;

import java.util.ArrayList;
import java.util.List;

/**
 * M3u8Adapter
 * Created by fanchen on 2018/9/17.
 */
public class M3u8Adapter extends BaseAdapter implements View.OnClickListener {

    private boolean isDeleteMode;
    private OnM3u8ControlListener onM3u8ControlListener;

    public M3u8Adapter(Context context) {
        super(context);
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_download_row;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new M3u8Holder(v);
    }

    public void setM3u8Files(List<M3u8File> m3u8Files){
        if (getList() == null) return;
        List<M3u8Warp> warps = new ArrayList<>();
        for (M3u8File file : m3u8Files) {
            warps.add(new M3u8Warp(file));
        }
        super.addAll(warps);
    }

    /**
     * @param isDeleteMode
     */
    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        notifyDataSetChanged();
    }

    public void setOnM3u8ControlListener(OnM3u8ControlListener onM3u8ControlListener) {
        this.onM3u8ControlListener = onM3u8ControlListener;
    }

    public void updataItem(List<M3u8File> m3u8Files, int state, String msg) {
        for (M3u8File file : m3u8Files) {
            updataItem(m3u8ToM3u8Warp(file), state, msg);
        }
    }

    public void updataItem(M3u8File m3u8File, int state, String msg) {
        M3u8Warp warp = m3u8ToM3u8Warp(m3u8File);
        updataItem(warp, state, msg);
    }

    public void updataItem(M3u8File m3u8File, String msg) {
        updataItem(m3u8File, -2, msg);
    }

    public void updataItem(M3u8 m3u8, String msg) {
        updataItem(m3u8, -2, msg);
    }

    public void updataItem(M3u8Warp warp, int state, String msg) {
        if (warp == null || warp.holder == null) return;
        M3u8Holder holder = (M3u8Holder) warp.holder;
        holder.progressText.setText(msg);
        holder.downloadLength.setText("");
        if (state != -2 && warp.m3u8File != null) warp.m3u8File.setState(state);
        if (state == M3u8State.INSTANCE.getSTETE_DOWNLOAD() || state == M3u8State.INSTANCE.getSTETE_NON()) {
            holder.btnPlay.setVisibility(View.GONE);
            holder.downloadControl.setVisibility(View.VISIBLE);
            holder.downloadControl.setImageResource(R.drawable.ic_action_download_pause);
        } else if (state == M3u8State.INSTANCE.getSTETE_ERROR() || state == M3u8State.INSTANCE.getSTETE_STOP()) {
            holder.btnPlay.setVisibility(View.GONE);
            holder.downloadControl.setVisibility(View.VISIBLE);
            holder.downloadControl.setImageResource(R.drawable.ic_action_download_start);
        } else if (state == M3u8State.INSTANCE.getSTETE_SUCCESS()) {
            holder.progressBar.setMax(100);
            holder.progressBar.setProgress(100);
            holder.btnPlay.setVisibility(View.VISIBLE);
            holder.downloadControl.setVisibility(View.GONE);
        }
    }

    public void updataItem(M3u8 m3u8, int state, String msg) {
        updataItem(m3u8ToM3u8Warp(m3u8), state, msg);
    }

    public void updataItem(M3u8 m3u8, int curr, int totel) {
        M3u8Warp warp = m3u8ToM3u8Warp(m3u8);
        if (warp == null) return;
        M3u8Holder holder = (M3u8Holder) warp.holder;
        holder.progressBar.setMax(totel);
        holder.progressBar.setProgress(curr);
        holder.progressText.setText("下载中...");
        holder.downloadLength.setText(String.format("%d/%d", curr, totel));
    }

    private M3u8Warp m3u8ToM3u8Warp(M3u8 m3u8) {
        List<M3u8Warp> m3u8Files = (List<M3u8Warp>) getList();
        for (M3u8Warp warp : m3u8Files) {
            if (warp.holder == null) return null;
            M3u8Holder holder = (M3u8Holder) warp.holder;
            M3u8Warp swarp = (M3u8Warp) holder.downloadName.getTag();
            if (warp.m3u8File.getUrl().equals(m3u8.getParentUrl()) && swarp == warp) {
                return warp;
            }
        }
        return null;
    }

    private M3u8Warp m3u8ToM3u8Warp(M3u8File m3u8) {
        List<M3u8Warp> m3u8Files = (List<M3u8Warp>) getList();
        for (M3u8Warp warp : m3u8Files) {
            if (warp.holder == null) return null;
            M3u8Holder holder = (M3u8Holder) warp.holder;
            M3u8Warp swarp = (M3u8Warp) holder.downloadName.getTag();
            if (warp.m3u8File.getUrl().equals(m3u8.getUrl()) && swarp == warp) {
                return warp;
            }
        }
        return null;
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position) return;
        M3u8Warp warp = (M3u8Warp) datas.get(position);
        M3u8Holder m3u8Holder = (M3u8Holder) (warp.holder = holder);
        m3u8Holder.downloadName.setTag(warp);
        m3u8Holder.downloadName.setText(warp.m3u8File.getM3u8VideoName());
        m3u8Holder.progressText.setText(warp.getState());
        m3u8Holder.progressBar.setMax(100);
        m3u8Holder.progressBar.setProgress(warp.getProgress());
        m3u8Holder.downloadLength.setText("");
        m3u8Holder.btnDelete.setTag(warp.m3u8File);
        m3u8Holder.btnDelete.setOnClickListener(this);
        m3u8Holder.downloadControl.setTag(warp.m3u8File);
        m3u8Holder.downloadControl.setOnClickListener(this);
        m3u8Holder.btnPlay.setTag(warp.m3u8File);
        m3u8Holder.btnPlay.setOnClickListener(this);
        m3u8Holder.btnDelete.setVisibility(View.GONE);
        setBtnState(m3u8Holder, warp.m3u8File);
        if (!isDeleteMode) return;
        m3u8Holder.btnPlay.setVisibility(View.GONE);
        m3u8Holder.downloadControl.setVisibility(View.GONE);
        m3u8Holder.btnDelete.setVisibility(View.VISIBLE);
    }

    private void setBtnState(M3u8Holder viewHolder, M3u8File entity) {
        if (entity.getState() == M3u8State.INSTANCE.getSTETE_DOWNLOAD()) {
            viewHolder.btnPlay.setVisibility(View.GONE);
            viewHolder.downloadControl.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_pause);
        } else if (entity.getState() == M3u8State.INSTANCE.getSTETE_ERROR()) {
            viewHolder.btnPlay.setVisibility(View.GONE);
            viewHolder.downloadControl.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_start);
        } else if (entity.getState() == M3u8State.INSTANCE.getSTETE_SUCCESS()) {
            viewHolder.progressBar.setProgress(100);
            viewHolder.btnPlay.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setVisibility(View.GONE);
        } else {
            viewHolder.btnPlay.setVisibility(View.GONE);
            viewHolder.downloadControl.setVisibility(View.VISIBLE);
            viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_start);
        }
    }

    @Override
    public void onClick(View v) {
        if (onM3u8ControlListener == null) return;
        M3u8File file = (M3u8File) v.getTag();
        switch (v.getId()) {
            case R.id.downloadControl:
                if (file.getState() == M3u8State.INSTANCE.getSTETE_DOWNLOAD()) {
                    onM3u8ControlListener.onControl(this, OnM3u8ControlListener.STOP, file);
                } else {
                    onM3u8ControlListener.onControl(this, OnM3u8ControlListener.START, file);
                }
                break;
            case R.id.btnDelete:
                onM3u8ControlListener.onControl(this, OnM3u8ControlListener.DELETE, file);
                break;
            case R.id.btnPlay:
                onM3u8ControlListener.onControl(this, OnM3u8ControlListener.PLAY, file);
                break;
        }
    }

    public void remove(M3u8File m3u8File) {
        List<M3u8Warp> list = (List<M3u8Warp>) getList();
        int position = -1;
        for (int i = 0; i < list.size(); i++) {
            if (m3u8File.getUrl().equals(list.get(i).m3u8File.getUrl())) {
                position = i;
            }
        }
        if (position != -1) {
            remove(position);
        }
    }

    public interface OnM3u8ControlListener {
        int DELETE = 0;
        int PLAY = 1;
        int START = 2;
        int STOP = 3;

        void onControl(BaseAdapter adapter, int control, M3u8File m3u8File);
    }

    private class M3u8Holder extends RecyclerView.ViewHolder {
        public TextView downloadName;//   文件名,如xxxx第几集
        public TextView downloadLength;//  大小：15M/88M
        public TextView progressText;// 网速/下载中/暂停中/等待中/
        public ProgressBar progressBar;//进度条
        public ImageButton downloadControl;// 最右边的控制按钮:下载/暂停
        public ImageButton btnDelete;// 删除按钮
        public ImageButton btnPlay;// 播放按钮

        public M3u8Holder(View itemView) {
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
}
