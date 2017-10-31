package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.entity.xiaobo.XiaoboVodBody;

import java.util.List;

/**
 * Created by fanchen on 2017/8/5.
 */
public class CouldAdapter extends BaseAdapter {

    public CouldAdapter(Context context) {
        super(context);
    }

    public CouldAdapter(Context context, List<IViewType> mList) {
        super(context, mList);
    }

    @Override
    public boolean hasFooterView() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return viewType == IViewType.TYPE_NORMAL ? new CouldViewHolder(v ) : new FooterViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if(datas == null || datas.size() <= position)return;
        if(viewType == IViewType.TYPE_FOOTER)return;
        CouldViewHolder viewHolder = (CouldViewHolder) holder;
        XiaoboVodBody body = (XiaoboVodBody) datas.get(position);
        viewHolder.mTitleTextView.setText(body.getTitle());
        viewHolder.mContentTextView.setText(body.toString());
    }

    @Override
    public int getLayout(int viewType) {
        return viewType == IViewType.TYPE_NORMAL ? R.layout.item_could_search :R.layout.item_load_footer;
    }

    private static class CouldViewHolder extends RecyclerView.ViewHolder{

        public TextView mTitleTextView;
        public TextView mContentTextView;

        public CouldViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            mContentTextView = (TextView) itemView.findViewById(R.id.tv_info);
        }
    }
}
