package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.VideoWeb;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fanchen on 2017/10/11.
 */
public class FreeVideoAdapter extends BaseAdapter{

    private PicassoWrap picassoWrap;

    public FreeVideoAdapter(Context context,Picasso picasso, List<IViewType> mList) {
        super(context, mList);
        picassoWrap = new PicassoWrap(picasso);
    }

    public FreeVideoAdapter(Context context,Picasso picasso) {
        super(context);
        picassoWrap = new PicassoWrap(picasso);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new ItemViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if(datas == null || datas.size() <= position)return;
        VideoWeb videoWeb = (VideoWeb) datas.get(position);
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.textView.setText(videoWeb.getName());
        picassoWrap.loadHorizontal(videoWeb.getCover(),viewHolder.imageView);
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_free_video;
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_cover_image);
            textView = (TextView) itemView.findViewById(R.id.tv_title_text);
        }
    }
}
