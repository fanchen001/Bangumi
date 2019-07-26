//package com.fanchen.imovie.fragment;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//
//import com.fanchen.imovie.activity.VideoPlayerActivity;
//import com.fanchen.imovie.adapter.LiveListAdapter;
//import com.fanchen.imovie.base.BaseAdapter;
//import com.fanchen.imovie.base.BaseRecyclerFragment;
//import com.fanchen.imovie.entity.dytt.DyttLive;
//import com.fanchen.imovie.entity.dytt.DyttRoot;
//import com.fanchen.imovie.retrofit.RetrofitManager;
//import com.fanchen.imovie.retrofit.service.DyttService;
//import com.fanchen.imovie.thread.AsyTaskQueue;
//import com.fanchen.imovie.util.DialogUtil;
//import com.google.gson.reflect.TypeToken;
//import com.squareup.picasso.Picasso;
//
//import java.lang.reflect.Type;
//import java.util.List;
//
///**
// * TvLiveListFragment
// * Created by fanchen on 2017/8/2.
// */
//public class TvLiveListFragment extends BaseRecyclerFragment implements BaseAdapter.OnItemLongClickListener {
//    public static final String KEY = "key";
//
//    private LiveListAdapter mLiveAdapter;
//    private String serializeKey;
//    private String key = "";
//
//    public static Fragment newInstance(String key) {
//        Fragment fragment = new TvLiveListFragment();
//        Bundle args = new Bundle();
//        args.putString(KEY, key);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    protected boolean hasLoad() {
//        return false;
//    }
//
//    @Override
//    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
//        key = getArguments().getString(KEY);
//        serializeKey = getClass().getSimpleName() + "_" + key;
//        super.initFragment(savedInstanceState, args);
//    }
//
//    @Override
//    public RecyclerView.LayoutManager getLayoutManager() {
//        return new BaseAdapter.LinearLayoutManagerWrapper(activity);
//    }
//
//    @Override
//    public BaseAdapter getAdapter(Picasso picasso) {
//        return mLiveAdapter = new LiveListAdapter(activity, picasso);
//    }
//
//    @Override
//    protected void setListener() {
//        super.setListener();
//        mLiveAdapter.setOnItemLongClickListener(this);
//    }
//
//    @Override
//    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
//        retrofit.enqueue(DyttService.class, callback, "liveVideo", key,"6560",Long.valueOf(System.currentTimeMillis()));
//    }
//
//    @Override
//    public void loadLocalData(AsyTaskQueue queue) {
//        queue.execute(taskListener);
//    }
//
//    @Override
//    protected boolean useLocalStorage() {
//        return true;
//    }
//
//    @Override
//    public String getSerializeKey() {
//        return serializeKey;
//    }
//
//    @Override
//    public int getStaleTime() {
//        return 2;
//    }
//
//    @Override
//    public Type getSerializeClass() {
//        return new TypeToken<DyttRoot<List<DyttLive>>>() {}.getType();
//    }
//
//    @Override
//    public void onItemClick(List<?> datas, View v, int position) {
//        if (!(datas.get(position) instanceof DyttLive)) return;
//        DyttLive body = (DyttLive) datas.get(position);
//        VideoPlayerActivity.startActivity(activity, body);
//    }
//
//    @Override
//    public boolean onItemLongClick(List<?> datas, View v, int position) {
//        if (!(datas.get(position) instanceof DyttLive)) return false;
//        DyttLive item = (DyttLive) datas.get(position);
//        DialogUtil.showOperationDialog(this, item, (List<DyttLive>) datas, position);
//        return true;
//    }
//
//    private RefreshRecyclerFragmentImpl<DyttRoot<List<DyttLive>>> callback = new RefreshRecyclerFragmentImpl<DyttRoot<List<DyttLive>>>() {
//
//        @Override
//        public void onSuccess(DyttRoot<List<DyttLive>> response) {
//            if (response.getBody() == null || mLiveAdapter == null) return;
//            List<DyttLive> body = response.getBody();
//            mLiveAdapter.addAll(body);
//        }
//
//    };
//
//    private QueryTaskListener<DyttRoot<List<DyttLive>>> taskListener = new QueryTaskListener<DyttRoot<List<DyttLive>>>() {
//
//        @Override
//        public void onSuccess(DyttRoot<List<DyttLive>> date) {
//            if (date.getBody() == null || mLiveAdapter == null) return;
//            List<DyttLive> body = date.getBody();
//            mLiveAdapter.addAll(body);
//        }
//
//    };
//
//}
