package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.ApkListActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.entity.apk.ApkItem;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * apk列表展示适配器
 * Created by Administrator on 2017/4/7.
 */
public class ApkListAdapter extends BaseAdapter {

    private PicassoWrap picasso;
    private boolean hasLoad;

    public ApkListAdapter(Context context, Picasso picasso, boolean hasLoad) {
        super(context);
        this.picasso = new PicassoWrap(picasso);
        this.hasLoad = hasLoad;
    }

    public ApkListAdapter(Context context, Picasso picasso, List<IViewType> mList) {
        super(context, mList);
        this.picasso = new PicassoWrap(picasso);
    }


    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return viewType == IViewType.TYPE_NORMAL ? new ApkListViewHolder(v ) : new FooterViewHolder(v);
    }

    @Override
    public boolean hasFooterView() {
        return hasLoad;
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if(viewType == IViewType.TYPE_FOOTER)return;
        ApkListViewHolder viewHolder = (ApkListViewHolder) holder;
        ApkItem animApkItem = (ApkItem) datas.get(position);
        viewHolder.mTitleTextView.setText(animApkItem.getTitle());
        viewHolder.mMessTextView.setText(animApkItem.getIntro());
        picasso.loadHorizontal(animApkItem.getCover(), ApkListActivity.class, viewHolder.mCoverImageView);
        picasso.loadHorizontal(animApkItem.getIco(), ApkListActivity.class,viewHolder.mIcoImageView);
    }

    @Override
    public int getLayout(int viewType) {
        return viewType == IViewType.TYPE_NORMAL ? R.layout.item_apk_list :R.layout.item_load_footer;
    }

    public static class ApkListViewHolder extends RecyclerView.ViewHolder {

        public ImageView mCoverImageView;
        public ImageView mIcoImageView;
        public TextView mMessTextView;
        public TextView mTitleTextView;

        public ApkListViewHolder(View itemView) {
            super(itemView);
            mCoverImageView = (ImageView) itemView.findViewById(R.id.iv_app_card_header);
            mIcoImageView = (ImageView) itemView.findViewById(R.id.iv_app_card_icon);
            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_app_card_title);
            mMessTextView = (TextView) itemView.findViewById(R.id.tv_app_card_intro);
        }
    }
}
