package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.WebActivity;
import com.fanchen.imovie.adapter.AcgMationAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.acg.AcgData;
import com.fanchen.imovie.entity.acg.AcgPosts;
import com.fanchen.imovie.entity.acg.AcgRoot;
import com.fanchen.imovie.entity.acg.AcgToken;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.service.Acg12Service;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 资讯速递
 * Created by fanchen on 2017/7/22.
 */
public class AcgMationFragment extends BaseRecyclerFragment {

    private AcgMationAdapter mMationAdapter;

    public static Fragment newInstance() {
        return new AcgMationFragment();
    }

    @Override
    protected boolean hasLoad() {
        return true;
    }

    @Override
    protected boolean hasRefresh() {
        return true;
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(activity);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mMationAdapter = new AcgMationAdapter(activity,picasso);
    }

    @Override
    public void loadData(Bundle arg,RetrofitManager retrofit,int page) {
        String token = "";
        if(appliction != null){
            token = appliction.mAcg12Token;
        }else if(IMovieAppliction.app != null){
            token = IMovieAppliction.app.mAcg12Token;
        }
        if(TextUtils.isEmpty(token)){
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),"appSecure=3wk9khscjfk&appId=4q6wnmmdzd");
            retrofit.enqueue(Acg12Service.class,stringCallback,"getToken",requestBody);
        }else{
            loadPosts(retrofit, page, token);
        }
    }

    @Override
    public void loadLocalData(AsyTaskQueue queue) {
        queue.execute(taskListener);
    }

    @Override
    protected boolean useLocalStorage() {
        return true;
    }

    @Override
    public Type getSerializeClass() {
        return new TypeToken<AcgRoot<AcgData>>(){}.getType();
    }

    @Override
    public String getSerializeKey() {
        return getClass().getSimpleName();
    }

    /**
     *
     * @param retrofit
     * @param page
     * @param token
     */
    private void loadPosts(RetrofitManager retrofit, int page, String token) {
        String body = "catId=5491&number=10&unsets%%5B%%5D=content&page=%d&token=%s";
        String format = String.format(body, page,token);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),format);
        retrofit.enqueue(Acg12Service.class, callback, "getPostsByCategory", requestBody);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if(!(datas.get(position) instanceof AcgPosts))return;
        AcgPosts posts = (AcgPosts) datas.get(position);
        WebActivity.startActivity(activity, posts.getTitle(), posts.getUrl());
    }

    private RefreshRecyclerFragmentImpl<AcgRoot<AcgData>> callback = new RefreshRecyclerFragmentImpl<AcgRoot<AcgData>>() {

        @Override
        public void onSuccess(AcgRoot<AcgData> response,boolean refresh) {
            if(response.getData() == null || mMationAdapter == null)return;
            mMationAdapter.setList(response.getData().getPosts(),refresh);
        }

    };

    private QueryTaskListener<AcgRoot<AcgData>> taskListener = new QueryTaskListener<AcgRoot<AcgData>>() {

        @Override
        public void onSuccess(AcgRoot<AcgData> date) {
            if(date.getData() == null || mMationAdapter == null)return;
            mMationAdapter.addAll(date.getData().getPosts());
        }

    };

    private RefreshCallback<AcgRoot<AcgToken>> stringCallback = new RefreshCallback<AcgRoot<AcgToken>>() {

        @Override
        public void onStart(int enqueueKey) {
            if(mSwipeRefreshLayout == null)return;
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onFinish(int enqueueKey) {
            showToast(getStringFix(R.string.success_token));
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            if(mSwipeRefreshLayout == null)return;
            showToast(getStringFix(R.string.error_token));
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onSuccess(int enqueueKey, AcgRoot<AcgToken> response) {
            if(response == null || response.getData() == null || activity == null)return;
            activity.appliction.mAcg12Token = response.getData().getToken();
            loadPosts(getRetrofitManager(), 1, response.getData().getToken());
        }

    };
}
