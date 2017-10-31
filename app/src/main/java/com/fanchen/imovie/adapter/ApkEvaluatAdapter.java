package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.ApkEvaluatActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.entity.apk.ApkEvaluat;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fanchen on 2017/7/19.
 */
public class ApkEvaluatAdapter extends BaseAdapter {

    private PicassoWrap picasso;

    public ApkEvaluatAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = new PicassoWrap(picasso);
    }

    public ApkEvaluatAdapter(Context context, Picasso picasso, List<IViewType> mList) {
        super(context, mList);
        this.picasso = new PicassoWrap(picasso);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new EvaluatingViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if(datas == null || datas.size() <= position)return;
        EvaluatingViewHolder viewHolder = (EvaluatingViewHolder) holder;
        ApkEvaluat evaluating = (ApkEvaluat) datas.get(position);
        viewHolder.timeTextView.setText(evaluating.getEdittime());
        viewHolder.titleTextView.setText(evaluating.getTitle());
        viewHolder.typeTextView.setText(evaluating.getWriter());
        picasso.loadHorizontal(evaluating.getCover(), ApkEvaluatActivity.class,viewHolder.imageView);

    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_acg_evaluating;
    }

    private static class EvaluatingViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView titleTextView;
        public TextView timeTextView;
        public TextView typeTextView;

        public EvaluatingViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_acg_cover);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_acg_title);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_acg_time);
            typeTextView = (TextView) itemView.findViewById(R.id.tv_acg_author);
        }
    }
}
