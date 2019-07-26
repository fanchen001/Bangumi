package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanchen.imovie.activity.WebActivity;
import com.fanchen.imovie.adapter.AcgCuteAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.acg.AcgData;
import com.fanchen.imovie.entity.acg.AcgPosts;
import com.fanchen.imovie.entity.acg.AcgRoot;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.Acg12Service;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 萌化应用
 * Created by fanchen on 2017/7/22.
 */
public class AcgCuteFragment extends BaseRecyclerFragment {

    private AcgCuteAdapter mCuteAdapter;

    public static Fragment newInstance() {
        return new AcgCuteFragment();
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(activity);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mCuteAdapter = new AcgCuteAdapter(activity);
    }

    @Override
    public void loadData(Bundle savedInstanceState,RetrofitManager retrofit,int page) {
        String token = activity.appliction.mAcg12Token;
        String body = "catId=1244&number=10&unsets%%5B%%5D=content&page=%d&token=%s";
        String format = String.format(body, page,token);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),format);
        getRetrofitManager().enqueue(Acg12Service.class,callback,"getPostsByCategory",requestBody);
    }

    @Override
    public void loadLocalData(AsyTaskQueue queue) {
        queue.execute(taskListener);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if(!(datas.get(position) instanceof AcgPosts))return;
        AcgPosts posts = (AcgPosts) datas.get(position);
        WebActivity.startActivity(activity, posts.getTitle(), posts.getUrl());
    }

    @Override
    protected boolean useLocalStorage() {
        return true;
    }

    @Override
    public String getSerializeKey() {
        return getClass().getSimpleName();
    }

    @Override
    public Type getSerializeClass() {
        return new TypeToken<AcgRoot<AcgData>>(){}.getType();
    }

    private RefreshRecyclerFragmentImpl<AcgRoot<AcgData>> callback = new RefreshRecyclerFragmentImpl<AcgRoot<AcgData>>() {

        @Override
        public void onSuccess(AcgRoot<AcgData> response,boolean refresh) {
            if(mCuteAdapter == null || response.getData() == null)return;
            mCuteAdapter.setList(response.getData().getPosts(),refresh);
        }

    };

    private QueryTaskListener<AcgRoot<AcgData>> taskListener = new QueryTaskListener<AcgRoot<AcgData>>() {

        @Override
        public void onSuccess(AcgRoot<AcgData> date) {
            if(date.getData() == null || mCuteAdapter == null)return;
            mCuteAdapter.addAll(date.getData().getPosts());
        }

    };
}
