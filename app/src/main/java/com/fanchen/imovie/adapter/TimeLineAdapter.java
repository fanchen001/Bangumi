package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.TimeLineActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IBangumiTimeTitle;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fanchen on 2017/9/20.
 */
public class TimeLineAdapter extends BaseAdapter {

    private PicassoWrap picassoWrap;

    public TimeLineAdapter(Context context, Picasso picasso) {
        super(context);
        picassoWrap = new PicassoWrap(picasso);
    }

    public TimeLineAdapter(Context context, Picasso picasso, List<IViewType> mList) {
        super(context, mList);
        picassoWrap = new PicassoWrap(picasso);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return viewType == IViewType.TYPE_NORMAL ? new ItemViewHolder(v) : new TitleViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        IViewType iViewType = datas.get(position);
        if (viewType == IViewType.TYPE_TITLE) {
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            IBangumiTimeTitle bangumiTitle = (IBangumiTimeTitle) iViewType;
            titleViewHolder.textView.setText(bangumiTitle.getTitle());
            picassoWrap.load(bangumiTitle.getDrawable(), TimeLineActivity.class, titleViewHolder.imageView);
        } else if (viewType == IViewType.TYPE_NORMAL) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            IBaseVideo bangumiItem = (IBaseVideo) iViewType;
            itemViewHolder.textView.setText(bangumiItem.getTitle());
            picassoWrap.loadHorizontal(bangumiItem.getCover(), TimeLineActivity.class, itemViewHolder.imageView);
        }
    }

    @Override
    public int getLayout(int viewType) {
        return viewType == IViewType.TYPE_NORMAL ? R.layout.item_time_line : R.layout.item_time_line_title;
    }

    public static class TitleViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;

        public TitleViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_timeline_ic);
            textView = (TextView) itemView.findViewById(R.id.item_title_timeline);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_line_img);
            textView = (TextView) itemView.findViewById(R.id.tv_line_title);
        }
    }
}
