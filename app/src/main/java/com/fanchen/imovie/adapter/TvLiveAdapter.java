package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * TvLiveAdapter
 */
public class TvLiveAdapter extends BaseAdapter{

    private PicassoWrap wrap;

    public TvLiveAdapter(Context context,Picasso picasso) {
        super(context);
        wrap = new PicassoWrap(picasso);
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_tv_live;
    }


    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new TvLiveHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        IBaseVideo iViewType = (IBaseVideo) datas.get(position);
        TvLiveHolder tvLiveHolder = (TvLiveHolder) holder;
        tvLiveHolder.titleTextView.setText(iViewType.getTitle());
        if(iViewType.getCover().contains("githubusercontent")){
            tvLiveHolder.imageView.setVisibility(View.GONE);
        }else{
            wrap.loadHorizontal(iViewType.getCover(),tvLiveHolder.imageView);
        }
    }

    private static class TvLiveHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleTextView;

        public TvLiveHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_tv_ico);
            titleTextView = (TextView) itemView.findViewById(R.id.iv_tv_title);
        }

    }
}
