package com.fanchen.imovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.ShortVideoTabActivity;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.entity.dytt.DyttShortVideo;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ShortVideoAdapter extends BaseAdapter {

    private PicassoWrap picassoWrap;
    private OnItemPlayClick playclick;

    public ShortVideoAdapter(Context context,Picasso picasso) {
        super(context);
        picassoWrap = new PicassoWrap(picasso);
    }

    public ShortVideoAdapter(Context context, Picasso picasso,List<IViewType> mList) {
        super(context, mList);
        picassoWrap = new PicassoWrap(picasso);
    }

    @Override
    public boolean hasFooterView() {
        return true;
    }

    public void setOnItemPlayClick(OnItemPlayClick playclick) {
        this.playclick = playclick;
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View v, int viewType) {
        return viewType == IViewType.TYPE_NORMAL ? new ShortVideoViewHolder(v) : new FooterViewHolder(v);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, List<IViewType> datas, int viewType, int position) {
        if(datas == null || datas.size() <= position)return;
        ShortVideoViewHolder videoViewHolder = (ShortVideoViewHolder) holder;
        DyttShortVideo video = (DyttShortVideo) datas.get(position);
        videoViewHolder.timeTextView.setText(video.getDuration());
        videoViewHolder.userTextView.setText(video.getWemedia().getTitle());
        videoViewHolder.countTextView.setText(video.getPlayCount());
        videoViewHolder.titleTextView.setText(video.getTitle());
        videoViewHolder.mPlayView.setOnClickListener(new PlayClickListener(position,getList()));
        picassoWrap.loadHorizontal(video.getCover(), ShortVideoTabActivity.class, videoViewHolder.coverImageView);
        picassoWrap.loadVertical(video.getWemedia().getHeadImg(), ShortVideoTabActivity.class,videoViewHolder.avatarImageView);
    }

    @Override
    public int getLayout(int viewType) {
        return viewType == IViewType.TYPE_NORMAL ? R.layout.item_short_video : R.layout.item_load_footer;
    }

    private class PlayClickListener implements View.OnClickListener{

        private int position;
        private List<?> mList;
        public PlayClickListener(int position,List<?> mList){
            this.position = position;
            this.mList = mList;
        }

        @Override
        public void onClick(View v) {
            if(playclick != null){
                playclick.onItemPlay(mList,position, (RelativeLayout) v.getParent());
            }
        }

    }

    private static class ShortVideoViewHolder extends RecyclerView.ViewHolder{
        public ImageView coverImageView;
        public TextView timeTextView;
        public TextView userTextView;
        public TextView countTextView;
        public TextView titleTextView;
        public RelativeLayout controlRelativeLayout;
        public ImageView avatarImageView;
        public View mPlayView;

        public ShortVideoViewHolder(View itemView) {
            super(itemView);
            userTextView = (TextView) itemView.findViewById(R.id.tv_super_video_user);
            countTextView = (TextView) itemView.findViewById(R.id.tv_super_video_count);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_super_video_title);
            coverImageView = (ImageView) itemView.findViewById(R.id.fl_super_video_layout_iv_cover);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_video_time);
            controlRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_player_control);
            avatarImageView = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mPlayView = itemView.findViewById(R.id.iv_super_video_play);
        }
    }

    /**
     *
     */
    public interface OnItemPlayClick {
        /**
         *
         * @param datas
         * @param position
         * @param view
         */
        void onItemPlay(List<?> datas,int position, RelativeLayout view);
    }
}
