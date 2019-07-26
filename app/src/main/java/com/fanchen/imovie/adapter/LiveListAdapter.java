//package com.fanchen.imovie.adapter;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.fanchen.imovie.R;
//import com.fanchen.imovie.base.BaseAdapter;
//import com.fanchen.imovie.entity.dytt.DyttLive;
//import com.fanchen.imovie.entity.dytt.DyttLiveEpgs;
//import com.fanchen.imovie.entity.face.IViewType;
//import com.fanchen.imovie.fragment.TvLiveListFragment;
//import com.fanchen.imovie.picasso.PicassoWrap;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
///**
// * LiveListAdapter
// * Created by fanchen on 2017/8/2.
// */
//public class LiveListAdapter extends BaseAdapter {
//
//    private PicassoWrap picasso;
//
//    public LiveListAdapter(Context context, Picasso picasso) {
//        super(context);
//        this.picasso = new PicassoWrap(picasso);
//    }
//
//    public LiveListAdapter(Context context, Picasso picasso, List<IViewType> mList) {
//        super(context, mList);
//        this.picasso = new PicassoWrap(picasso);
//    }
//
//    @Override
//    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
//        return new LiveItemViewHolder(v);
//    }
//
//    @Override
//    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
//        if (datas == null || datas.size() <= position) return;
//        LiveItemViewHolder viewHolder = (LiveItemViewHolder) holder;
//        DyttLive body = (DyttLive) datas.get(position);
//        List<DyttLiveEpgs> epgs = body.getEpgs();
//        if(epgs != null && epgs.size() > 0){
//            viewHolder.currentTextView.setText(String.format(context.getString(R.string.current_live), epgs.get(0).getTitle()));
//            viewHolder.nextTextView.setText(String.format(context.getString(R.string.next_live), epgs.get(epgs.size() - 1).getTitle()));
//        }
//        viewHolder.titleTextView.setText(body.getTitle());
//        picasso.loadHorizontal(body.getThumb_x(), TvLiveListFragment.class,viewHolder.imageView);
//    }
//
//    @Override
//    public int getLayout(int viewType) {
//        return R.layout.item_live_list;
//    }
//
//
//    protected static class LiveItemViewHolder extends RecyclerView.ViewHolder {
//        public TextView titleTextView;
//        public TextView currentTextView;
//        public TextView nextTextView;
//        public ImageView imageView;
//
//        public LiveItemViewHolder(View itemView) {
//            super(itemView);
//            titleTextView = (TextView) itemView.findViewById(R.id.tv_text);
//            currentTextView = (TextView) itemView.findViewById(R.id.tv_current);
//            nextTextView = (TextView) itemView.findViewById(R.id.tv_next);
//            imageView = (ImageView) itemView.findViewById(R.id.iv_live_image);
//        }
//    }
//}
