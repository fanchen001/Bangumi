package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.VideoTabActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.view.TriangleLabelView;

import java.util.List;


/**
 * Created by fanchen on 2017/7/18.
 */
public class RecomAdapter extends BaseAdapter {
    private PicassoWrap picasso;

    public RecomAdapter(Context context, PicassoWrap picasso) {
        super(context);
        this.picasso = picasso;
    }

    public RecomAdapter(Context context, PicassoWrap picasso, List<IViewType> mList) {
        super(context, mList);
        this.picasso = picasso;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new VideoViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position) return;
        IVideo video = (IVideo) datas.get(position);
        VideoViewHolder itemViewHolder = (VideoViewHolder) holder;
        itemViewHolder.titleTextView.setText(video.getTitle());
        String extras = video.getExtras();
        itemViewHolder.tipTextView.setText(extras);
        itemViewHolder.tipTextView.setVisibility(TextUtils.isEmpty(extras) ? View.GONE : View.VISIBLE);
        String danmaku = video.getDanmaku();
        itemViewHolder.triangTextView.setPrimaryText(danmaku);
        itemViewHolder.triangTextView.setVisibility(TextUtils.isEmpty(danmaku) ? View.GONE : View.VISIBLE);
        picasso.loadVertical(video.getCover(), VideoTabActivity.class, itemViewHolder.imageView);
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_video_vertical;
    }

    private static class VideoViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TriangleLabelView triangTextView;
        public TextView titleTextView;
        public TextView tipTextView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_img);
            triangTextView = (TriangleLabelView) itemView.findViewById(R.id.tlv_score);
            titleTextView = (TextView) itemView.findViewById(R.id.item_title);
            tipTextView = (TextView) itemView.findViewById(R.id.item_tip);
        }

    }

    public void setPicasso(PicassoWrap picasso) {
        this.picasso = picasso;
    }
}
