package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.AcgTabActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.entity.acg.AcgPosts;
import com.fanchen.imovie.entity.acg.AcgThumbnail;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.picasso.download.RefererDownloader;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fanchen on 2017/7/23.
 */
public class AcgCuteAdapter extends BaseAdapter {
    private static final String REFERER = "https://acg12.com/category/acg-game/android-game/";
    private PicassoWrap picasso;

    public AcgCuteAdapter(Context context) {
        super(context);
        this.picasso = new PicassoWrap(context,new RefererDownloader(context,REFERER));
    }

    public AcgCuteAdapter(Context context, List<IViewType> mList) {
        super(context, mList);
        this.picasso = new PicassoWrap(context,new RefererDownloader(context,REFERER));
    }

    @Override
    public boolean hasFooterView() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return viewType == IViewType.TYPE_FOOTER ? new FooterViewHolder(v) : new CuteItemViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position || viewType != IViewType.TYPE_NORMAL) return;
        CuteItemViewHolder viewHolder = (CuteItemViewHolder) holder;
        AcgPosts posts = (AcgPosts) datas.get(position);
        viewHolder.timeTextView.setText(posts.getDate() != null ? posts.getDate().getHuman() : "");
        viewHolder.titleTextView.setText(posts.getTitle());
        viewHolder.typeTextView.setText(posts.getAuthor() != null ? posts.getAuthor().getName() : "");
        AcgThumbnail thumbnail = posts.getThumbnail();
        if (thumbnail != null && thumbnail.getUrl() != null)
            picasso.loadVertical(thumbnail.getUrl(), AcgTabActivity.class, viewHolder.imageView);

    }

    @Override
    public int getLayout(int viewType) {
        return viewType == IViewType.TYPE_FOOTER ? R.layout.item_load_footer : R.layout.item_acg_evaluating;
    }

    private static class CuteItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleTextView;
        public TextView timeTextView;
        public TextView typeTextView;

        public CuteItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_acg_cover);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_acg_title);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_acg_time);
            typeTextView = (TextView) itemView.findViewById(R.id.tv_acg_author);
        }
    }
}
