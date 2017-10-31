package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTask;
import com.arialyy.aria.core.inf.IEntity;
import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.DownloadEntityWrap;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/10/3.
 */
public class DownloadAdapter extends BaseAdapter {

    private boolean isDeleteMode;

    public DownloadAdapter(Context context) {
        super(context);
    }

    public DownloadAdapter(Context context, List<IViewType> mList) {
        super(context, mList);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new DownloadViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position) return;
        DownloadViewHolder viewHolder = (DownloadViewHolder) holder;
        DownloadEntityWrap wrap = (DownloadEntityWrap) datas.get(position);
        DownloadEntity entity = wrap.getEntity();
        viewHolder.progressBar.setMax(100);
        int percent = entity.getPercent();
        viewHolder.progressBar.setProgress(percent <= 0 ? (int) (entity.getCurrentProgress() * 100L / entity.getFileSize()) : percent);
        viewHolder.downloadLength.setText(entity.getConvertSpeed());
        viewHolder.downloadName.setText(entity.getFileName());
        viewHolder.btnDelete.setTag(entity);
        viewHolder.btnDelete.setOnClickListener(controlListener);
        viewHolder.downloadControl.setTag(entity);
        viewHolder.downloadControl.setOnClickListener(controlListener);
        viewHolder.btnPlay.setTag(entity);
        viewHolder.btnPlay.setOnClickListener(controlListener);
        viewHolder.btnDelete.setVisibility(View.GONE);
        switch (entity.getState()) {
            case IEntity.STATE_STOP:
                viewHolder.progressText.setText("停止");
                viewHolder.btnPlay.setVisibility(View.GONE);
                viewHolder.downloadControl.setVisibility(View.VISIBLE);
                viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_start);
                break;
            case IEntity.STATE_RUNNING:
                viewHolder.progressText.setText("下载中");
                viewHolder.btnPlay.setVisibility(View.GONE);
                viewHolder.downloadControl.setVisibility(View.VISIBLE);
                viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_pause);
                break;
            case IEntity.STATE_FAIL:
                viewHolder.progressText.setText("失败");
                viewHolder.btnPlay.setVisibility(View.GONE);
                viewHolder.downloadControl.setVisibility(View.VISIBLE);
                viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_start);
                break;
            case IEntity.STATE_WAIT:
                viewHolder.progressText.setText("等待");
                viewHolder.btnPlay.setVisibility(View.GONE);
                viewHolder.downloadControl.setVisibility(View.VISIBLE);
                viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_pause);
                break;
            case IEntity.STATE_COMPLETE:
                viewHolder.progressText.setText("完成");
                viewHolder.progressBar.setProgress(100);
                viewHolder.btnPlay.setVisibility(View.VISIBLE);
                viewHolder.downloadControl.setVisibility(View.GONE);
                break;
            case IEntity.STATE_PRE:
            case IEntity.STATE_POST_PRE:
                viewHolder.progressText.setText("预处理");
                viewHolder.btnPlay.setVisibility(View.GONE);
                viewHolder.downloadControl.setVisibility(View.VISIBLE);
                viewHolder.downloadControl.setImageResource(R.drawable.ic_action_download_pause);
            default:
                viewHolder.progressText.setText("未知状态");
        }
        if (isDeleteMode) {
            viewHolder.btnPlay.setVisibility(View.GONE);
            viewHolder.downloadControl.setVisibility(View.GONE);
            viewHolder.btnDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_download_row;
    }

    private static class DownloadViewHolder extends RecyclerView.ViewHolder {
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

    public void update(DownloadTask task) {
        long time = System.currentTimeMillis();
        if (task == null || getList() == null) return;
        for (DownloadEntityWrap e : (List<DownloadEntityWrap>) getList()) {
            if (task.getDownloadUrl().equals(e.getEntity().getDownloadUrl())) {
                e.getEntity().setPercent(task.getPercent());
                e.getEntity().setState(task.getState());
                e.getEntity().setConvertSpeed(task.getConvertSpeed());
                notifyItemChanged(e.getPosition());
            }
        }
    }

    public void addAll(List<DownloadEntity> all, String suffix) {
        if (all == null) return;
        List<DownloadEntityWrap> newList = new ArrayList<>();
        if (!TextUtils.isEmpty(suffix)) {
            String[] split = suffix.split("/");
            for (String s : split) {
                for (DownloadEntity e : all) {
                    if (e.getDownloadUrl().toLowerCase().contains(s.toLowerCase())) {
                        newList.add(new DownloadEntityWrap(e, newList.size()));
                    } else if (e.getFileName().toLowerCase().contains(s.toLowerCase())) {
                        newList.add(new DownloadEntityWrap(e, newList.size()));
                    }
                }
            }
        }
        super.addAll(newList);
    }

    /**
     * @param entity
     */
    public void remove(DownloadEntity entity) {
        if (entity == null) return;
        int position = -1;
        for (DownloadEntityWrap e : (List<DownloadEntityWrap>) getList()) {
            position++;
            if (entity.getDownloadUrl().equals(e.getEntity().getDownloadUrl())) {
                break;
            }
        }
        if (position != -1) {
            remove(position);
        }
    }

    /**
     * @param isDeleteMode
     */
    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        notifyDataSetChanged();
    }

    private View.OnClickListener controlListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            DownloadEntity entity = (DownloadEntity) v.getTag();
            if (entity == null) return;
            switch (v.getId()) {
                case R.id.downloadControl:
                    switch (entity.getState()) {
                        case IEntity.STATE_STOP:
                        case IEntity.STATE_FAIL:
                            Aria.download(((BaseActivity) context).appliction).load(entity.getUrl()).start();
                            entity.setState(IEntity.STATE_RUNNING);
                            break;
                        case IEntity.STATE_WAIT:
                        case IEntity.STATE_RUNNING:
                            Aria.download(((BaseActivity) context).appliction).load(entity.getUrl()).stop();
                            entity.setState(IEntity.STATE_STOP);
                            break;
                    }
                    notifyDataSetChanged();
                    break;
                case R.id.btnDelete:
                    DialogUtil.showMaterialDialog(context, context.getString(R.string.delete_file), new DeleteClickListener(entity));
                    break;
                case R.id.btnPlay:
                    String fileName = entity.getFileName();
                    if (!TextUtils.isEmpty(fileName) && fileName.contains(".apk")) {
                        SystemUtil.installApk(context, entity.getDownloadPath());
                    } else if (!TextUtils.isEmpty(fileName) && fileName.contains(".mp4")) {
                        VideoPlayerActivity.startActivity(context, entity);
                    }
                    break;
            }
        }
    };

    private class DeleteClickListener implements OnButtonClickListener {
        private DownloadEntity entity;

        public DeleteClickListener(DownloadEntity entity) {
            this.entity = entity;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            if (btn == OnButtonClickListener.RIGHT) {
                Aria.download(((BaseActivity) context).appliction).load(entity.getUrl()).cancel(true);
                remove(entity);
            }
            dialog.dismiss();
        }
    }
}