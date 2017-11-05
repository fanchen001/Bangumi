package com.fanchen.imovie.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.inf.IEntity;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/7/18.
 */
public class EpisodeAdapter extends BaseAdapter {

    private boolean parent = false;
    private boolean hasLoad = false;
    private Drawable doneDrawable;
    private Drawable inDrawable;
    private Drawable failDrawable;
    private BaseActivity activity;

    public EpisodeAdapter(BaseActivity context) {
        super(context);
        this.activity = context;
        doneDrawable = context.getResources().getDrawable(R.drawable.badge_download_done);
        failDrawable = context.getResources().getDrawable(R.drawable.badge_download_failed);
        inDrawable = context.getResources().getDrawable(R.drawable.badge_download_inprogress);
    }

    public EpisodeAdapter(BaseActivity context, boolean hasLoad, boolean parent) {
        super(context);
        this.parent = parent;
        this.hasLoad = hasLoad;
        this.activity = context;
        doneDrawable = context.getResources().getDrawable(R.drawable.badge_download_done);
        failDrawable = context.getResources().getDrawable(R.drawable.badge_download_failed);
        inDrawable = context.getResources().getDrawable(R.drawable.badge_download_inprogress);
    }

    public EpisodeAdapter(BaseActivity context, List<IViewType> mList) {
        super(context, mList);
        this.activity = context;
        doneDrawable = context.getResources().getDrawable(R.drawable.badge_download_done);
        failDrawable = context.getResources().getDrawable(R.drawable.badge_download_failed);
        inDrawable = context.getResources().getDrawable(R.drawable.badge_download_inprogress);
    }

    @Override
    public boolean hasFooterView() {
        return hasLoad;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return viewType == IViewType.TYPE_FOOTER ? new FooterViewHolder(v) : new EpisodeViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position || viewType == IViewType.TYPE_FOOTER) return;
        if (parent || hasLoad) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.itemView.setLayoutParams(layoutParams);
        }
        IVideoEpisode trailer = (IVideoEpisode) datas.get(position);
        EpisodeViewHolder viewHolder = (EpisodeViewHolder) holder;
        viewHolder.textView.setText(trailer.getTitle());
        ((CardView) viewHolder.itemView).setCardBackgroundColor(context.getResources().getColor(R.color.white));
        viewHolder.textView.setCompoundDrawables(null, null, null, null);
        switch (trailer.getDownloadState()) {
            case IVideoEpisode.DOWNLOAD_ERROR:
                failDrawable.setBounds(0, 0, failDrawable.getMinimumWidth(), failDrawable.getMinimumHeight());
                viewHolder.textView.setCompoundDrawables(null, null, failDrawable, null);
                break;
            case IVideoEpisode.DOWNLOAD_SELECT:
                ((CardView) viewHolder.itemView).setCardBackgroundColor(context.getResources().getColor(R.color.myPrimaryColor));
                break;
            case IVideoEpisode.DOWNLOAD_RUN:
                inDrawable.setBounds(0, 0, inDrawable.getMinimumWidth(), inDrawable.getMinimumHeight());
                viewHolder.textView.setCompoundDrawables(null, null, inDrawable, null);
                break;
            case IVideoEpisode.DOWNLOAD_SUCCESS:
                doneDrawable.setBounds(0, 0, doneDrawable.getMinimumWidth(), doneDrawable.getMinimumHeight());
                viewHolder.textView.setCompoundDrawables(null, null, doneDrawable, null);
                break;
            default:
                break;
        }
    }

    @Override
    public int getLayout(int viewType) {
        return viewType == IViewType.TYPE_FOOTER ? R.layout.item_load_footer : R.layout.item_video_episode;
    }

    /**
     * @return
     */
    public List<IVideoEpisode> getSelect() {
        List<IVideoEpisode> all = new ArrayList<>();
        for (IVideoEpisode e : (List<IVideoEpisode>) getList()) {
            if (e.getDownloadState() == IVideoEpisode.DOWNLOAD_SELECT) {
                all.add(e);
            }
        }
        return all;
    }

    /**
     * @param selectAll
     */
    public void setSelectAll(boolean selectAll) {
        for (IVideoEpisode e : (List<IVideoEpisode>) getList()) {
            if (e.getDownloadState() != IVideoEpisode.DOWNLOAD_SUCCESS
                    || e.getDownloadState() != IVideoEpisode.DOWNLOAD_RUN
                    || e.getDownloadState() != IVideoEpisode.DOWNLOAD_ERROR) {
                e.setDownloadState(selectAll ? IVideoEpisode.DOWNLOAD_SELECT : IVideoEpisode.DOWNLOAD_NON);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * @return
     */
    public boolean isSelectAll() {
        for (IVideoEpisode e : (List<IVideoEpisode>) getList()) {
            if (e.getDownloadState() == IVideoEpisode.DOWNLOAD_NON) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addAll(List<? extends IViewType> all) {
        new Thread(new AddRunnable(all)).start();
    }

    private static class EpisodeViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public EpisodeViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_video_episode);
        }
    }

    private class AddRunnable implements Runnable{

        private List<? extends IViewType> all;

        private AddRunnable(List<? extends IViewType> all){
            this.all = all;
        }

        @Override
        public void run() {
            if (activity == null || all == null || all.size() <= 0) return;
            List<DownloadEntity> taskList = activity.getDownloadReceiver().getSimpleTaskList();
            if (taskList == null || taskList.size() <= 0) return;
            for (IVideoEpisode e : (List<IVideoEpisode>) all) {
                for (DownloadEntity entity : taskList) {
                    if (e.getUrl().equals(entity.getMd5Code())) {
                        if (entity.getState() == IEntity.STATE_COMPLETE) {
                            e.setDownloadState(IVideoEpisode.DOWNLOAD_SUCCESS);
                            e.setFilePath(entity.getDownloadPath());
                        }else if (entity.getState() == IEntity.STATE_FAIL) {
                            e.setDownloadState(IVideoEpisode.DOWNLOAD_ERROR);
                        } else {
                            e.setDownloadState(IVideoEpisode.DOWNLOAD_RUN);
                        }
                    }
                }
            }
            EpisodeAdapter.super.addAll(all);
        }

    };
}
