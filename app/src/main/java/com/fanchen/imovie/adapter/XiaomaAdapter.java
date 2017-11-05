package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.entity.xiaoma.XiaomaSearch;
import com.fanchen.imovie.fragment.XiaomaFragment;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.view.TriangleLabelView;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by fanchen on 2017/11/4.
 */
public class XiaomaAdapter extends BaseAdapter {

    private PicassoWrap picassoWrap;

    public XiaomaAdapter(Context context,Picasso picasso) {
        super(context);
        picassoWrap = new PicassoWrap(picasso);
    }

    public XiaomaAdapter(Context context,Picasso picasso, List<IViewType> mList) {
        super(context, mList);
        picassoWrap = new PicassoWrap(picasso);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return new XiaomaViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if(datas == null || datas.size() < position || position < 0)return;
        XiaomaViewHolder viewHolder = (XiaomaViewHolder) holder;
        XiaomaSearch search = (XiaomaSearch) datas.get(position);
        viewHolder.mTitleTextView.setText(search.getTitle());
        viewHolder.mInfoTextView.setText(search.getDesc());
        viewHolder.mUrlTextView.setText(search.getSourceLink());
        if(search.getNavType() == 1){
            viewHolder.mInlineLabelView.setVisibility(View.VISIBLE);
        }else{
            viewHolder.mInlineLabelView.setVisibility(View.GONE);
        }
        picassoWrap.loadVertical(search.getImg(), XiaomaFragment.class,viewHolder.mCoverImageView);
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.item_xiaoma_search;
    }

    public static class XiaomaViewHolder extends RecyclerView.ViewHolder{
        public ImageView mCoverImageView;
        public TextView mTitleTextView;
        public TextView mInfoTextView;
        public TextView mUrlTextView;
        public TriangleLabelView mInlineLabelView;

        public XiaomaViewHolder(View itemView) {
            super(itemView);
            mCoverImageView = (ImageView) itemView.findViewById(R.id.iv_cover);
            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            mInfoTextView = (TextView) itemView.findViewById(R.id.tv_info);
            mUrlTextView = (TextView) itemView.findViewById(R.id.tv_info_url);
            mInlineLabelView = (TriangleLabelView) itemView.findViewById(R.id.tlv_inline);
        }
    }
}
