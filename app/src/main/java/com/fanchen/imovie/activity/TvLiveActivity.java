package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.TvLiveAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerActivity;
import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.HlyyTvService;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 电视直播
 * Created by fanchen on 2018/8/6.
 */
public class TvLiveActivity extends BaseRecyclerActivity {

    private String classService = HlyyTvService.class.getName();
    private TvLiveAdapter mLiveAdapter;

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, TvLiveActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected String getActivityTitle() {
        return getString(R.string.tv_live);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.GridLayoutManagerWrapper(this, 2);
    }

    @Override
    protected BaseAdapter getAdapter(Picasso picasso) {
        return mLiveAdapter = new TvLiveAdapter(this, picasso);
    }

    @Override
    protected void loadData(RetrofitManager retrofit, int page) {
        retrofit.enqueue(classService, callback, "liveList", "");
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (datas == null || datas.size() <= position) return;
        IBaseVideo video = (IBaseVideo) datas.get(position);
        LivePlayerActivity.startActivity(this, video);
    }

    private RefreshRecyclerActivityImpl<List<IBaseVideo>> callback = new RefreshRecyclerActivityImpl<List<IBaseVideo>>() {

        @Override
        public void onSuccess(int enqueueKey, List<IBaseVideo> response) {
            if (response == null) return;
            mLiveAdapter.setList(response,isRefresh());
        }

    };
}
