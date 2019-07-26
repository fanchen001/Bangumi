package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.TimeLineAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerActivity;
import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IBangumiTimeRoot;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.TucaoService;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 番剧播放时间表
 * Created by fanchen on 2017/9/20.
 */
public class TimeLineActivity extends BaseRecyclerActivity {

    private TimeLineAdapter mTimeLineAdapter;

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, TimeLineActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.GridLayoutManagerWrapper(this, 3);
    }

    @Override
    protected BaseAdapter getAdapter(Picasso picasso) {
        return mTimeLineAdapter = new TimeLineAdapter(this, picasso);
    }

    @Override
    protected void loadData(RetrofitManager retrofit, int page) {
        retrofit.enqueue(TucaoService.class, callback, "timeLine");
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.time_line);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IBaseVideo)) return;
        IBaseVideo video = (IBaseVideo) datas.get(position);
        SearchBangumiActivity.startActivity(this, video.getTitle());
    }

    private RefreshRecyclerActivityImpl<IBangumiTimeRoot> callback = new RefreshRecyclerActivityImpl<IBangumiTimeRoot>() {

        @Override
        public void onSuccess(int enqueueKey, IBangumiTimeRoot response) {
            if (response == null || !response.isSuccess() ||mTimeLineAdapter == null || mRecyclerView == null) return;
            List<? extends IViewType> adapterList = response.getAdapterList();
            mTimeLineAdapter.setList(adapterList,isRefresh());
            mRecyclerView.scrollToPosition(response.getPosition());
            mTimeLineAdapter.setLoad(false);
            mTimeLineAdapter.setLoading(false);
        }

    };
}
