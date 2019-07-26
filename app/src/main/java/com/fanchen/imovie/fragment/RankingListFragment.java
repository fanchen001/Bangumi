package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.adapter.BangumiListAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.TucaoService;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fanchen on 2017/9/20.
 */
public class RankingListFragment extends BaseRecyclerFragment {

    private static final String TID = "tid";
    private static final String DATE = "date";

    private String tid;
    private String date;
    private BangumiListAdapter mVideoListAdapter;

    /**
     * @param id
     * @return
     */
    public static Fragment newInstance(String id, String date) {
        Fragment fragment = new RankingListFragment();
        Bundle args = new Bundle();
        args.putString(TID, id);
        args.putString(DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        tid = getArguments().getString(TID);
        date = getArguments().getString(DATE);
        super.initFragment(savedInstanceState, args);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(activity, BaseAdapter.LinearLayoutManagerWrapper.VERTICAL, false);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mVideoListAdapter = new BangumiListAdapter(activity, picasso);
    }

    @Override
    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
        retrofit.enqueue(TucaoService.class, callback, "ranking", tid, date);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IVideo)) return;
        IVideo video = (IVideo) datas.get(position);
        VideoDetailsActivity.startActivity(activity, video);
    }

    private RefreshRecyclerFragmentImpl<IBangumiMoreRoot> callback = new RefreshRecyclerFragmentImpl<IBangumiMoreRoot>() {

        @Override
        public void onSuccess(IBangumiMoreRoot response,boolean refresh) {
            if (!response.isSuccess() || mVideoListAdapter == null) return;
            mVideoListAdapter.setList(response.getList(),refresh);
            mVideoListAdapter.setLoad(false);
            mVideoListAdapter.setLoading(false);
        }

    };
}
