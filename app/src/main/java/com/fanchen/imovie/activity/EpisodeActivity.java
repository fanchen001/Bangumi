package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.EpisodeAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerActivity;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 《更多剧集》 列表Activity
 * Created by fanchen on 2017/8/22.
 */
public class EpisodeActivity extends BaseRecyclerActivity {
    public static final String VIDEO = "video";

    private IVideoDetails mVideoDetails;
    private EpisodeAdapter mEpisodeAdapter;

    public static void startActivity(Context context,IVideoDetails mVideoDetails) {
        try {
            Intent intent = new Intent(context, EpisodeActivity.class);
            intent.putExtra(VIDEO, mVideoDetails);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        mVideoDetails = getIntent().getParcelableExtra(VIDEO);
        super.initActivity(savedState, inflater);
    }

    @Override
    protected boolean hasLoad() {
        return true;
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.bangumi_episode);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.GridLayoutManagerWrapper(this, 3);
    }

    @Override
    protected BaseAdapter getAdapter(Picasso picasso) {
        return mEpisodeAdapter = new EpisodeAdapter(this, false,true);
    }

    @Override
    protected void loadData(RetrofitManager retrofitManager,int page) {
        if(mVideoDetails == null){
            showToast(getString(R.string.error_non));
            finish();
            return;
        }
        mEpisodeAdapter.addAll(mVideoDetails.getEpisodes());
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IVideoEpisode)) return;
        IVideoEpisode episode = (IVideoEpisode) datas.get(position);
        VideoPlayerActivity.Companion.startActivity(this, mVideoDetails, episode);
    }
}
