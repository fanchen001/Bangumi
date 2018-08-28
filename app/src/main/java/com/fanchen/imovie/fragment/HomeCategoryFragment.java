package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.VideoTabActivity;
import com.fanchen.imovie.adapter.CategoryAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.VideoCategory;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fanchen on 2017/11/9.
 */
public class HomeCategoryFragment extends BaseRecyclerFragment{

    private CategoryAdapter mCategoryAdapter;

    public static Fragment newInstance() {
        return new HomeCategoryFragment();
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mCategoryAdapter = new CategoryAdapter(activity,picasso);
    }

    @Override
    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
        mSwipeRefreshLayout.setEnabled(false);
        try {
            String json = new String(StreamUtil.stream2bytes(activity.getAssets().open("category.json")));
            List<VideoCategory> list = new Gson().fromJson(json, new TypeToken<List<VideoCategory>>() {}.getType());
            mCategoryAdapter.addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if(!(datas.get(position) instanceof VideoCategory))return;
        VideoCategory category = (VideoCategory) datas.get(position);
        VideoTabActivity.startActivity(activity, category.getTitle(),category.getType());
    }


}
