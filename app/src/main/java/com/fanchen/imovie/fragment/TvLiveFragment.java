package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanchen.imovie.activity.LivePlayerActivity;
import com.fanchen.imovie.adapter.TvLiveAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * TvLiveFragment
 * Created by fanchen on 2018/10/10.
 */
public class TvLiveFragment extends BaseRecyclerFragment {

    public static final String CLASS = "class";
    public static final String PATH = "path";

    private String classService = "";
    private String path = "";
    private TvLiveAdapter mLiveAdapter;

    public static Fragment newInstance(String classService, String path) {
        Fragment fragment = new TvLiveFragment();
        Bundle args = new Bundle();
        args.putString(CLASS, classService);
        args.putString(PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        classService = args.getString(CLASS, "");
        path = args.getString(PATH, "");
        super.initFragment(savedInstanceState, args);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.GridLayoutManagerWrapper(activity,2);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mLiveAdapter = new TvLiveAdapter(activity, picasso);
    }

    @Override
    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
        retrofit.enqueue(classService, callback, "liveList", path);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (datas == null || datas.size() <= position) return;
        IBaseVideo video = (IBaseVideo) datas.get(position);
        LivePlayerActivity.startActivity(activity, video);
    }

    private RefreshRecyclerFragmentImpl<List<IBaseVideo>> callback = new RefreshRecyclerFragmentImpl<List<IBaseVideo>>() {

        @Override
        public void onSuccess(List<IBaseVideo> response,boolean refresh) {
            if (response == null) return;
            //第一次加载或者是刷新
            mLiveAdapter.setList(response,refresh);
        }

    };
}
