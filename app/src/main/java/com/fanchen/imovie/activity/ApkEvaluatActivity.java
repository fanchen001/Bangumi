package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.ApkEvaluatAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerActivity;
import com.fanchen.imovie.entity.apk.ApkData;
import com.fanchen.imovie.entity.apk.ApkEvaluat;
import com.fanchen.imovie.entity.apk.ApkParamData;
import com.fanchen.imovie.entity.apk.ApkParamUser;
import com.fanchen.imovie.entity.apk.ApkRoot;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.MoeapkService;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 游戏评测
 * Created by fanchen on 2017/8/22.
 */
public class ApkEvaluatActivity extends BaseRecyclerActivity {

    private Gson gson = new Gson();
    private ApkEvaluatAdapter mEvaluatingAdapter;

    /**
     *
     * @param context
     */
    public static void startActivity(Context context){
        try {
            Intent intent = new Intent(context,ApkEvaluatActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.game_pc);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(this, BaseAdapter.LinearLayoutManagerWrapper.VERTICAL, false);
    }

    @Override
    protected BaseAdapter getAdapter(Picasso picasso) {
        return mEvaluatingAdapter = new ApkEvaluatAdapter(this,picasso);
    }

    @Override
    protected void loadData(RetrofitManager retrofit,int page) {
        String user = gson.toJson(new ApkParamUser());
        String data = gson.toJson(new ApkParamData(page));
        String format = String.format("data=%s&user=%s", URLEncoder.encode(data), URLEncoder.encode(user));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),format);
        getRetrofitManager().enqueue(MoeapkService.class, callback, "getArticleList", requestBody);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if(!(datas.get(position) instanceof ApkEvaluat))return;
        ApkEvaluat apkEvaluat = (ApkEvaluat) datas.get(position);
        WebActivity.startActivity(this,apkEvaluat.getTitle(),apkEvaluat.getUrl());
    }

    private RefreshRecyclerActivityImpl<ApkRoot<ApkData<ApkEvaluat>>> callback = new RefreshRecyclerActivityImpl<ApkRoot<ApkData<ApkEvaluat>>>() {

        @Override
        public void onSuccess(int enqueueKey, ApkRoot<ApkData<ApkEvaluat>> response) {
            if (response == null || response.getData() == null || mEvaluatingAdapter == null)return;
            mEvaluatingAdapter.addAll(response.getData().getList(),isRefresh());
        }

    };
}
