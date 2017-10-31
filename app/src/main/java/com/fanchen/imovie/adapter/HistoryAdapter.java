package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.HistoryActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.entity.bmob.VideoHistory;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.util.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 视频收藏Adapter
 * Created by fanchen on 2017/7/24.
 */
public class HistoryAdapter extends BaseAdapter {

    private PicassoWrap picasso;

    public HistoryAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = new PicassoWrap(picasso);
    }

    public HistoryAdapter(Context context, Picasso picasso, List<IViewType> mList) {
        super(context, mList);
        this.picasso = new PicassoWrap(picasso);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new VideoItemViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position) return;
        VideoItemViewHolder viewHolder = (VideoItemViewHolder) holder;
        VideoHistory history = (VideoHistory) datas.get(position);
        viewHolder.titleTextView.setText(history.getTitle());
        viewHolder.timeTextView.setText(String.format("播放时间:%s", history.getTime()));
        viewHolder.positionTextView.setText(String.format("上次播放:%s", DateUtil.secToTime((int)(history.getPlayPosition() / 1000))));
        picasso.loadHorizontal(history.getCover(), HistoryActivity.class,viewHolder.imageView);
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_history;
    }

    protected static class VideoItemViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView timeTextView;
        public TextView positionTextView;
        public ImageView imageView;

        public VideoItemViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_title_text);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time_text);
            imageView = (ImageView) itemView.findViewById(R.id.iv_cover_image);
            positionTextView = (TextView) itemView.findViewById(R.id.tv_positon_text);
        }
    }
}
