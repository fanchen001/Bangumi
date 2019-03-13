package com.fanchen.imovie.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.BangumiListActivity;
import com.fanchen.imovie.activity.VideoTabActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IBangumiTitle;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.fragment.HomeIndexFragment;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.view.TriangleLabelView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * VideoListAdapter
 * Created by fanchen on 2017/9/23.
 */
public class VideoListAdapter extends BaseAdapter{

    private PicassoWrap picasso;
    private boolean hasLoad = true;

    public VideoListAdapter(Context context, Picasso pic) {
        super(context);
        picasso = new PicassoWrap(pic);
    }

    public VideoListAdapter(Context context, Picasso pic,boolean hasLoad) {
        super(context);
        picasso = new PicassoWrap(pic);
        this.hasLoad = hasLoad;
    }

    public VideoListAdapter(Context context, Picasso pic, List<IViewType> mList) {
        super(context, mList);
        picasso = new PicassoWrap(pic);
    }

    @Override
    public boolean hasFooterView() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        if(viewType == IViewType.TYPE_NORMAL){
            return new VideoViewHolder(v);
        }else if(viewType == IViewType.TYPE_TITLE){
            return new TitleViewHolder(v);
        }else{
            return new FooterViewHolder(v);
        }
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (viewType == IViewType.TYPE_NORMAL) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            IVideo video = (IVideo) datas.get(position);
            String extras = video.getExtras();
            String danmaku = video.getDanmaku();
            if(extras != null && extras.trim().length() > 0){
                videoViewHolder.tipTextView.setVisibility(View.VISIBLE);
                videoViewHolder.tipTextView.setText(extras);
            }else{
                videoViewHolder.tipTextView.setVisibility(View.GONE);
            }
            if(danmaku != null && danmaku.trim().length() > 0){
                videoViewHolder.triangTextView.setVisibility(View.VISIBLE);
                videoViewHolder.triangTextView.setSecondaryText(danmaku);
            }else{
                videoViewHolder.triangTextView.setVisibility(View.GONE);
            }
            videoViewHolder.titleTextView.setText(video.getTitle());
            picasso.loadVertical(video.getCover(), VideoTabActivity.class,videoViewHolder.imageView);
        }else if(viewType == IViewType.TYPE_TITLE){
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            IBangumiTitle bangumiTitle = (IBangumiTitle) datas.get(position);
            titleViewHolder.textView.setText(bangumiTitle.getTitle());
            if (!bangumiTitle.hasMore()) {
                titleViewHolder.view.setVisibility(View.GONE);
            } else {
                titleViewHolder.view.setVisibility(View.VISIBLE);
                titleViewHolder.view.setOnClickListener(new MoreClickListener((Activity)context, bangumiTitle,true));
            }
            picasso.load(bangumiTitle.getDrawable(), HomeIndexFragment.class, titleViewHolder.imageView);
        }
    }

    @Override
    public int getLayout(int viewType) {
        if(viewType == IViewType.TYPE_NORMAL){
            return R.layout.item_video_vertical;
        }else if(viewType == IViewType.TYPE_TITLE){
            return R.layout.item_home_title;
        }else if(hasLoad){
            return R.layout.item_load_footer;
        }else{
            return R.layout.item_non_footer;
        }
    }

    @Override
    public void addData(Object data) {
        if(data instanceof IHomeRoot){
            addAll(((IHomeRoot)data).getAdapterResult());
        }
    }

    private static class VideoViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TriangleLabelView triangTextView;
        public TextView titleTextView;
        public TextView tipTextView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_img);
            triangTextView = (TriangleLabelView) itemView.findViewById(R.id.tlv_score);
            titleTextView = (TextView) itemView.findViewById(R.id.item_title);
            tipTextView = (TextView) itemView.findViewById(R.id.item_tip);
        }

    }

    private static class MoreClickListener implements View.OnClickListener {

        private Activity activity;
        private IBangumiTitle homeResult;
        private boolean hasLoad;

        public MoreClickListener(Activity activity, IBangumiTitle homeResult,boolean hasLoad) {
            this.activity = activity;
            this.homeResult = homeResult;
            this.hasLoad = hasLoad;
        }

        @Override
        public void onClick(View v) {
            BangumiListActivity.startActivity(activity, homeResult, hasLoad);
        }

    }
}
