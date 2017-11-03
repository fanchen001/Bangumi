package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.CollectTabActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.entity.bmob.VideoCollect;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.picasso.download.RefererDownloader;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 专题收藏Adapter
 * Created by fanchen on 2017/7/24.
 */
public class CollectAdapter extends BaseAdapter {

    private PicassoWrap picasso;
    private int collectType = VideoCollect.TYPE_VIDEO;

    public CollectAdapter(Context context, Picasso picasso,int collectType) {
        super(context);
        this.picasso = new PicassoWrap(picasso);
        this.collectType = collectType;
    }

    public CollectAdapter(Context context, Picasso picasso, int collectType,List<IViewType> mList) {
        super(context, mList);
        this.picasso = new PicassoWrap(picasso);
        this.collectType = collectType;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return collectType == VideoCollect.TYPE_VIDEO ? new VItemViewHolder(v) : new HItemViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if(datas == null || datas.size() <= position || viewType != IViewType.TYPE_NORMAL)return;
        VideoCollect videoCollect = (VideoCollect) datas.get(position);
        if(collectType == VideoCollect.TYPE_VIDEO){
            VItemViewHolder itemViewHolder = (VItemViewHolder) holder;
            String extras = videoCollect.getExtras();
            if(TextUtils.isEmpty(extras)){
                itemViewHolder.lastTextView.setVisibility(View.GONE);
            }else{
                itemViewHolder.lastTextView.setText(videoCollect.getExtras());
            }
            itemViewHolder.titleTextView.setText(videoCollect.getTitle());
            itemViewHolder.timeTextView.setText(videoCollect.getTime());
            if(!TextUtils.isEmpty(videoCollect.getCoverReferer())){
                new PicassoWrap(new Picasso.Builder(context).downloader(new RefererDownloader(context, videoCollect.getCoverReferer())).build()).loadVertical(videoCollect.getCover(), CollectTabActivity.class,itemViewHolder.imageView);
            }else{
                picasso.loadVertical(videoCollect.getCover(), CollectTabActivity.class,itemViewHolder.imageView);
            }
        }else{
            HItemViewHolder itemViewHolder = (HItemViewHolder) holder;
            itemViewHolder.startTextView.setText(videoCollect.getExtras());
            itemViewHolder.titleTextView.setText(videoCollect.getTitle());
            itemViewHolder.endTextView.setText(videoCollect.getTime());
            if(!TextUtils.isEmpty(videoCollect.getCoverReferer())){
                new PicassoWrap(new Picasso.Builder(context).downloader(new RefererDownloader(context, videoCollect.getCoverReferer())).build()).loadVertical(videoCollect.getCover(), CollectTabActivity.class,itemViewHolder.imageView);
            }else{
                picasso.loadVertical(videoCollect.getCover(), CollectTabActivity.class,itemViewHolder.imageView);
            }
        }
    }

    @Override
    public int getLayout(int viewType) {
        return collectType == VideoCollect.TYPE_VIDEO ? R.layout.item_collect_vertical : R.layout.item_collect_horizontal;
    }

    private static class VItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView;
        public TextView lastTextView;
        public TextView timeTextView;

        public VItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_img);
            titleTextView = (TextView) itemView.findViewById(R.id.item_title);
            lastTextView = (TextView) itemView.findViewById(R.id.item_last);
            timeTextView = (TextView) itemView.findViewById(R.id.item_time);
        }
    }

    private static class HItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView;
        public TextView startTextView;
        public TextView endTextView;

        public HItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_collect_image);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_collect_text);
            startTextView = (TextView) itemView.findViewById(R.id.tv_collect_start);
            endTextView = (TextView) itemView.findViewById(R.id.tv_collect_end);
        }
    }
}
