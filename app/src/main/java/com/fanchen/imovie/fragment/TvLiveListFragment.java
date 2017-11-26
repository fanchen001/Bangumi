package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.adapter.LiveListAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.dytt.DyttLiveBody;
import com.fanchen.imovie.entity.dytt.DyttRoot;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.DyttService;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.util.DialogUtil;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by fanchen on 2017/8/2.
 */
public class TvLiveListFragment extends BaseRecyclerFragment implements BaseAdapter.OnItemLongClickListener {
    public static final String KEY = "key";

    private LiveListAdapter mLiveAdapter;
    private String serializeKey;
    private String key = "";

    public static Fragment newInstance(String key) {
        Fragment fragment = new TvLiveListFragment();
        Bundle args = new Bundle();
        args.putString(KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean hasLoad() {
        return false;
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        key = getArguments().getString(KEY);
        serializeKey = getClass().getSimpleName() + "_" + key;
        super.initFragment(savedInstanceState, args);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(activity);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mLiveAdapter = new LiveListAdapter(activity, picasso);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mLiveAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public void loadData(Bundle savedInstanceState,RetrofitManager retrofit, int page) {
        retrofit.enqueue(DyttService.class, callback, "live");
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
    public int getStaleTime() {
        return 2;
    }

    @Override
    public Type getSerializeClass() {
        return new TypeToken<DyttRoot<List<DyttLiveBody>>>(){}.getType();
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof DyttLiveBody)) return;
        DyttLiveBody body = (DyttLiveBody) datas.get(position);
        VideoPlayerActivity.startActivity(activity, body);
    }

    @Override
    public boolean onItemLongClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof DyttLiveBody)) return false;
        DyttLiveBody item = (DyttLiveBody) datas.get(position);
        DialogUtil.showOperationDialog(this,item,(List<DyttLiveBody>)datas,position);
        return true;
    }

    private RefreshRecyclerFragmentImpl<DyttRoot<List<DyttLiveBody>>> callback = new RefreshRecyclerFragmentImpl<DyttRoot<List<DyttLiveBody>>>() {

        @Override
        public void onSuccess(DyttRoot<List<DyttLiveBody>> response) {
            if(response.getBody() == null || mLiveAdapter == null)return;
            setAdapterList(response);
        }

    };

    private QueryTaskListener<DyttRoot<List<DyttLiveBody>>> taskListener = new QueryTaskListener<DyttRoot<List<DyttLiveBody>>>() {

        @Override
        public void onSuccess(DyttRoot<List<DyttLiveBody>> date) {
            if(date.getBody() == null || mLiveAdapter == null)return;
            setAdapterList(date);
        }

    };

    /**
     *
     * @param response
     */
    private void setAdapterList(DyttRoot<List<DyttLiveBody>> response) {
        List<DyttLiveBody> body = response.getBody();
        for (int i = 0; i < body.size(); i++) {
            DyttLiveBody liveBody = body.get(i);
            if (!key.equals(liveBody.getArea())) {
                body.remove(liveBody);
                i--;
            }
        }
        mLiveAdapter.addAll(body);
    }

}
