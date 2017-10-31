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
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fanchen on 2017/7/23.
 */
public class AcgMationAdapter extends BaseAdapter {

    private PicassoWrap picasso;

    public AcgMationAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = new PicassoWrap(picasso);
    }

    public AcgMationAdapter(Context context, Picasso picasso, List<IViewType> mList) {
        super(context, mList);
        this.picasso = new PicassoWrap(picasso);
    }

    @Override
    public boolean hasFooterView() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return viewType == IViewType.TYPE_FOOTER ? new FooterViewHolder(v) : new MationItemViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position || viewType != IViewType.TYPE_NORMAL) return;
        MationItemViewHolder viewHolder = (MationItemViewHolder) holder;
        AcgPosts posts = (AcgPosts) datas.get(position);
        viewHolder.authorTextView.setText(posts.getAuthor() != null ? posts.getAuthor().getName() : "");
        viewHolder.titleTextView.setText(posts.getTitle());
        viewHolder.lastTextView.setText(posts.getDate() != null ? posts.getDate().getHuman() : "");
        AcgThumbnail thumbnail = posts.getThumbnail();
        if (thumbnail != null && thumbnail.getUrl() != null)
            picasso.load(thumbnail.getUrl(), AcgTabActivity.class, viewHolder.imageView);
    }

    @Override
    public int getLayout(int viewType) {
        return viewType == IViewType.TYPE_FOOTER ? R.layout.item_load_footer : R.layout.item_acg_mation;
    }

    protected static class MationItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView;
        public TextView lastTextView;
        public TextView authorTextView;

        public MationItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_acg_card_header);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_acg_card_title);
            lastTextView = (TextView) itemView.findViewById(R.id.tv_acg_card_time);
            authorTextView = (TextView) itemView.findViewById(R.id.tv_acg_card_author);
        }
    }
}
