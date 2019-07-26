package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.ShortVideoAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.dytt.DyttRoot;
import com.fanchen.imovie.entity.dytt.DyttShortVideo;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.DyttService;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by fanchen on 2017/9/22.
 */
public class ShortVideoFragment extends BaseRecyclerFragment{

    public static final String TID = "tid";

    private BaseAdapter.LinearLayoutManagerWrapper mLayoutManager;
    private ShortVideoAdapter mVideoAdapter;
    private String serializeKey;
    private String tid ;

    public static Fragment newInstance(String key) {
        Fragment fragment = new ShortVideoFragment();
        Bundle args = new Bundle();
        args.putString(TID, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean hasLoad() {
        return true;
    }

    @Override
    protected void setListener() {
        super.setListener();
        getRecyclerView().addOnChildAttachStateChangeListener((RecyclerView.OnChildAttachStateChangeListener) activity);
        mVideoAdapter.setOnItemPlayClick((ShortVideoAdapter.OnItemPlayClick) activity);
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        tid = getArguments().getString(TID);
        serializeKey = getClass().getSimpleName() + "_" + tid;
        super.initFragment(savedInstanceState, args);
    }


    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager == null ? mLayoutManager = new BaseAdapter.LinearLayoutManagerWrapper(activity) : mLayoutManager;
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mVideoAdapter = new ShortVideoAdapter(activity,picasso);
    }

    @Override
    public void loadData(Bundle savedInstanceState,RetrofitManager retrofit, int page) {
        retrofit.enqueue(DyttService.class,callback,"shortVideo",tid,Integer.valueOf(page));
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
    public String getSerializeKey() {
        return serializeKey;
    }

    @Override
    public Type getSerializeClass() {
        return new TypeToken<DyttRoot<List<DyttShortVideo>>>(){}.getType();
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {

    }

    private RefreshRecyclerFragmentImpl<DyttRoot<List<DyttShortVideo>>> callback = new RefreshRecyclerFragmentImpl<DyttRoot<List<DyttShortVideo>>>() {

        @Override
        public void onSuccess(DyttRoot<List<DyttShortVideo>> response,boolean refresh) {
            if(mVideoAdapter == null)return;
            List<DyttShortVideo> body = response.getBody();
            if(body != null && body.size() > 0){
                mVideoAdapter.setList(body,refresh);
                mVideoAdapter.setLoad(true);
            }else{
                showSnackbar(getStringFix(R.string.not_more));
                mVideoAdapter.setLoad(false);
            }
        }

    };

    private QueryTaskListener<DyttRoot<List<DyttShortVideo>>> taskListener = new QueryTaskListener<DyttRoot<List<DyttShortVideo>>>() {

        @Override
        public void onSuccess(DyttRoot<List<DyttShortVideo>> date) {
            if(mVideoAdapter == null )return ;
            List<DyttShortVideo> body = date.getBody();
            if(body != null && body.size() > 0){
                mVideoAdapter.addAll(body);
                mVideoAdapter.setLoad(true);
            }else{
                showSnackbar(getStringFix(R.string.not_more));
                mVideoAdapter.setLoad(false);
            }
        }

    };

}
