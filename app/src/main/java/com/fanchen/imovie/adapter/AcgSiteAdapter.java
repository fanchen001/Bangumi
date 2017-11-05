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
import com.fanchen.imovie.view.TriangleLabelView;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by fanchen on 2017/7/23.
 */
public class AcgSiteAdapter extends BaseAdapter {

    private static final String REFERER = "https://acg12.com/category/pixiv/pixiv-daily/";

    private PicassoWrap picasso;

    public AcgSiteAdapter(Context context) {
        super(context);
        this.picasso = new PicassoWrap(context,new RefererDownloader(context,REFERER));
    }

    public AcgSiteAdapter(Context context, List<IViewType> mList) {
        super(context, mList);
        this.picasso = new PicassoWrap(context,new RefererDownloader(context,REFERER));
    }

    @Override
    public boolean hasFooterView() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return viewType == IViewType.TYPE_FOOTER ? new FooterViewHolder(v) : new AcgItemViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (datas == null || datas.size() <= position || viewType != IViewType.TYPE_NORMAL) return;
        AcgItemViewHolder viewHolder = (AcgItemViewHolder) holder;
        AcgPosts posts = (AcgPosts) datas.get(position);
        viewHolder.authorTextView.setText(posts.getAuthor() != null ? posts.getAuthor().getName() : "");
        viewHolder.titleTextView.setText(posts.getTitle());
        viewHolder.lastTextView.setText(posts.getDate() != null ? posts.getDate().getHuman() : "");
        viewHolder.triangleLabelView.setPrimaryText(String.valueOf(posts.getViews()));
        AcgThumbnail thumbnail = posts.getThumbnail();
        if (thumbnail != null && thumbnail.getUrl() != null)
            picasso.load(thumbnail.getUrl(), AcgTabActivity.class, viewHolder.imageView);
    }

    @Override
    public int getLayout(int viewType) {
        return viewType == IViewType.TYPE_FOOTER ? R.layout.item_load_footer : R.layout.item_acg_image;
    }

    protected static class AcgItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleTextView;
        public TextView lastTextView;
        public TextView authorTextView;
        public TriangleLabelView triangleLabelView;

        public AcgItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_img);
            titleTextView = (TextView) itemView.findViewById(R.id.item_title);
            lastTextView = (TextView) itemView.findViewById(R.id.item_last);
            authorTextView = (TextView) itemView.findViewById(R.id.item_author);
            triangleLabelView = (TriangleLabelView) itemView.findViewById(R.id.item_tlv_info);
        }
    }
}
