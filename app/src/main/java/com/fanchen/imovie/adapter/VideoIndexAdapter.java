package com.fanchen.imovie.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.BangumiListActivity;
import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.activity.VideoTabActivity;
import com.fanchen.imovie.activity.WebActivity;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IBangumiRoot;
import com.fanchen.imovie.entity.face.IBangumiTitle;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoBanner;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.fragment.HomeIndexFragment;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.view.TriangleLabelView;
import com.fanchen.imovie.view.pager.IBanner;
import com.fanchen.imovie.view.pager.LoopViewPager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * videolist首页Adapter
 * Created by fanchen on 2017/7/12.
 */
public class VideoIndexAdapter extends BaseAdapter {

    private IBangumiRoot index;
    private PicassoWrap picasso;
    private BaseActivity activity;
    private HeaderViewHolder mHeaderHolder;

    public VideoIndexAdapter(BaseActivity context, Picasso picasso) {
        super(context);
        this.picasso = new PicassoWrap(picasso);
        this.activity = context;
    }

    public VideoIndexAdapter(BaseActivity context, Picasso picasso, List<IViewType> mList) {
        super(context, mList);
        this.picasso = new PicassoWrap(picasso);
        this.activity = context;
    }

    @Override
    public boolean hasHeaderView() {
        return index != null && index.getHomeBanner() != null && !index.getHomeBanner().isEmpty();
    }

    @Override
    public boolean hasFooterView() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == IViewType.TYPE_HEADER) {
            holder = new HeaderViewHolder(v);
        } else if (viewType == IViewType.TYPE_TITLE) {
            holder = new TitleViewHolder(v);
        } else if (viewType == IViewType.TYPE_FOOTER) {
            holder = new RecyclerView.ViewHolder(v){};
        }else {
            holder = new VideoViewHolder(v);
        }
        return holder;
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if (viewType == IViewType.TYPE_HEADER) {
            if (mHeaderHolder == null) mHeaderHolder = (HeaderViewHolder) holder;
            if (index != null) {
                mHeaderHolder.itemView.setVisibility(View.VISIBLE);
                mHeaderHolder.bannerView.setOnLoadImageViewListener(imageViewLoader);
                mHeaderHolder.bannerView.setOnBannerItemClickListener(bannerItemClickListener);
                mHeaderHolder.bannerView.setLoopData(index.getHomeBanner());
                if (mHeaderHolder.bannerView.hasLoop() && !mHeaderHolder.bannerView.isLoop()) {
                    mHeaderHolder.bannerView.startLoop();
                }
            } else {
                mHeaderHolder.itemView.setVisibility(View.GONE);
            }
        } else {
            IViewType iViewType = datas.get(position);
            if (iViewType.getViewType() == IViewType.TYPE_TITLE) {
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                IBangumiTitle bangumiTitle = (IBangumiTitle) iViewType;
                titleViewHolder.textView.setText(bangumiTitle.getTitle());
                if (!bangumiTitle.hasMore()) {
                    titleViewHolder.view.setVisibility(View.GONE);
                } else {
                    titleViewHolder.view.setVisibility(View.VISIBLE);
                    titleViewHolder.view.setOnClickListener(new MoreClickListener(activity, bangumiTitle, true));
                }
                picasso.load(bangumiTitle.getDrawable(), HomeIndexFragment.class, titleViewHolder.imageView);
            } else if (iViewType.getViewType() == IViewType.TYPE_NORMAL) {
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
                picasso.loadVertical(video.getCover(), VideoTabActivity.class, videoViewHolder.imageView);
            }else if (viewType == IViewType.TYPE_FOOTER){

            }
        }
    }

    @Override
    public int getLayout(int viewType) {
        int layout = -1;
        if (viewType == IViewType.TYPE_HEADER) {
            layout = R.layout.item_banner;
        } else if (viewType == IViewType.TYPE_TITLE) {
            layout = R.layout.item_home_title;
        } else if (viewType == IViewType.TYPE_FOOTER) {
            layout = R.layout.item_non_footer;
        } else {
            layout = R.layout.item_video_vertical;
        }
        return layout;
    }

    /**
     * @param index
     */
    @Override
    public void addData(Object index) {
        if(index instanceof IBangumiRoot){
            this.index = (IBangumiRoot) index;
            clear();
            addAll(this.index.getAdapterResult(), false);
            notifyDataSetChanged();
        }
    }

    /**
     * @return
     */
    public LoopViewPager getLoopViewPager() {
        return mHeaderHolder != null ? mHeaderHolder.bannerView : null;
    }

    private LoopViewPager.OnBannerItemClickListener bannerItemClickListener = new LoopViewPager.OnBannerItemClickListener() {

        @Override
        public void onBannerClick(int index, ArrayList<? extends IBanner> banner) {
            if (banner == null || banner.size() <= index) return;
            IVideoBanner<?> iBanner = (IVideoBanner<?>) banner.get(index);
            if (iBanner.getBannerType() == IBanner.TYPE_WEB) {
                WebActivity.startActivity(context, iBanner.getUrl());
            } else {
                VideoDetailsActivity.startActivity(context, iBanner.getId(), iBanner.getServiceClass());
            }
        }

    };

    private static class MoreClickListener implements View.OnClickListener {

        private Activity activity;
        private IBangumiTitle homeResult;
        private boolean hasLoad;

        public MoreClickListener(Activity activity, IBangumiTitle homeResult, boolean hasLoad) {
            this.activity = activity;
            this.homeResult = homeResult;
            this.hasLoad = hasLoad;
        }

        @Override
        public void onClick(View v) {
            BangumiListActivity.startActivity(activity, homeResult, hasLoad);
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

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public LoopViewPager bannerView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            bannerView = (LoopViewPager) itemView.findViewById(R.id.lvpl_banner);
        }
    }

    private LoopViewPager.OnLoadImageViewListener imageViewLoader = new LoopViewPager.OnLoadImageViewListener() {


        @Override
        public View createBannerView(Context context) {
            return LayoutInflater.from(context).inflate(R.layout.item_banner_view, null);
        }

        @Override
        public void onLoadImageView(View view, IBanner<?> parameter) {
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_banner_image);
            TextView textView = (TextView) view.findViewById(R.id.tv_banner_title);
            textView.setText(parameter.getTitle());
            picasso.loadHorizontal(parameter.getCover(), HomeIndexFragment.class, imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

    };

}
