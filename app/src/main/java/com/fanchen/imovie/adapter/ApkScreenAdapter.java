package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.entity.apk.ApkDetails;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * APK详情页图片展示adapter
 * Created by fanchen on 2017/7/6.
 */
public class ApkScreenAdapter extends BaseAdapter {

    private PicassoWrap picasso;

    public ApkScreenAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = new PicassoWrap(picasso);
    }

    public ApkScreenAdapter(Context context, List<IViewType> mList, Picasso picasso) {
        super(context, mList);
        this.picasso = new PicassoWrap(picasso);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new ScreenViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        ScreenViewHolder viewHolder = (ScreenViewHolder) holder;
        ApkDetails.ScreenShots screenShots = (ApkDetails.ScreenShots) datas.get(position);
        picasso.load(screenShots.getSourceurl(),viewHolder.imageView);
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_screen_image;
    }

    /**
     *
     */
    public static class ScreenViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ScreenViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) ((ViewGroup) itemView).getChildAt(0);
        }
    }
}
