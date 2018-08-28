package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.VideoCategory;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.fragment.HomeCategoryFragment;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.view.TriangleLabelView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fanchen on 2017/3/15.
 */
public class CategoryAdapter extends BaseAdapter {

    private PicassoWrap picassoWrap;

    public CategoryAdapter(Context context, Picasso picasso, List<IViewType> mList) {
        super(context, mList);
        this.picassoWrap = new PicassoWrap(picasso);
    }

    public CategoryAdapter(Context context, Picasso picasso) {
        super(context);
        this.picassoWrap = new PicassoWrap(picasso);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new CategoryViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        CategoryViewHolder viewHolder = (CategoryViewHolder) holder;
        VideoCategory category = (VideoCategory) datas.get(position);
        viewHolder.labelView.setVisibility(category.isHot() ? View.VISIBLE : View.GONE);
        picassoWrap.load(category.getDrawable(), HomeCategoryFragment.class, viewHolder.imageView);
        viewHolder.textView.setText(category.getTitle());
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_home_category;
    }


    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;
        public TriangleLabelView labelView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_img_region);
            textView = (TextView) itemView.findViewById(R.id.item_title_region);
            labelView = (TriangleLabelView) itemView.findViewById(R.id.tlv_hot);
        }
    }
}
