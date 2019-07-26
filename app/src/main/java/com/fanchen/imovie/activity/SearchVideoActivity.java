package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.BangumiListAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerActivity;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.JrenService;
import com.fanchen.imovie.retrofit.service.S80Service;
import com.fanchen.imovie.util.SecurityUtil;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.List;

/**
 * 其他视频[布米米][五弹幕]等搜索页面
 * Created by fanchen on 2017/9/24.
 */
public class SearchVideoActivity extends BaseRecyclerActivity {

    public static final String WORD = "word";
    public static final String CLASS_NAME = "className";
    public static final String MULTIPLE = "multiple";
    public static final String PAGE_START = "start";
    public static final String HAS_LOAD = "load";

    private String className;
    private int multiple;
    private String word;
    private boolean hasLoad;
    private BangumiListAdapter mListAdapter;

    /**
     *
     * @param context
     * @param word
     * @param className
     * @param pageStart
     * @param multiple
     * @param hasLoad
     */
    public static void  startActivity(Context context, String word,String className,int pageStart, int multiple, boolean hasLoad) {
        try {
            Intent intent = new Intent(context,SearchVideoActivity.class);
            intent.putExtra(WORD,word);
            intent.putExtra(PAGE_START,pageStart);
            intent.putExtra(CLASS_NAME,className);
            intent.putExtra(MULTIPLE,multiple);
            intent.putExtra(HAS_LOAD,hasLoad);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param context
     * @param word
     * @param className
     * @param pageStart
     * @param hasLoad
     */
    public static void  startActivity(Context context, String word,String className,int pageStart, boolean hasLoad) {
        startActivity(context,word,className,pageStart,1,hasLoad);
    }

    /**
     *
     * @param context
     * @param word
     * @param className
     * @param pageStart
     * @param multiple
     */
    public static void  startActivity(Context context, String word,String className, int pageStart,int multiple) {
        startActivity(context,word,className,pageStart,multiple,true);
    }

    /**
     *
     * @param context
     * @param word
     * @param className
     * @param pageStart
     */
    public static void  startActivity(Context context, String word,String className,int pageStart) {
        startActivity(context,word,className,pageStart,1,true);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        setPageStart(getIntent().getIntExtra(PAGE_START, 1));
        className = getIntent().getStringExtra(CLASS_NAME);
        word = getIntent().getStringExtra(WORD);
        hasLoad = getIntent().getBooleanExtra(HAS_LOAD, false);
        multiple = getIntent().getIntExtra(MULTIPLE, 1);
        super.initActivity(savedState, inflater);
    }

    @Override
    protected boolean hasLoad() {
        return hasLoad;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(this);
    }

    @Override
    protected BaseAdapter getAdapter(Picasso picasso) {
        return mListAdapter = new BangumiListAdapter(this,picasso);
    }

    @Override
    protected void loadData(RetrofitManager retrofit, int page) {
        //吉人動漫的搜索比較奇葩
        //需要特殊處理
        if (JrenService.class.getName().equals(className)) {
            //vod-search-pg-2-wd-你.html
//            String formatUrl = String.format("https://jren100.moe/search/%s", URLEncoder.encode(word));
//            String format = String.format("{\"paged\":%d,\"kw\":\"%s\",\"tags\":[],\"cat\":[],\"cats\":[2]}", page, word);
//            String encode = SecurityUtil.encode(format.getBytes());
//            retrofit.enqueue(className, callback, "search", formatUrl, encode);
            String format = String.format("vod-search-pg-%d-wd-%s.html",page,word);
            retrofit.enqueue(className, callback, "search", format);
        } else if (S80Service.class.getName().equals(className)) {
            //s80也需要特殊处理
            retrofit.enqueue(className, callback, "search", word);
        } else {
            Integer integer = Integer.valueOf(page * multiple);
            retrofit.enqueue(className, callback, "search", new Object[]{integer, word});
        }
    }

    @Override
    protected String getActivityTitle() {
        return String.format(getString(R.string.search_mart), word);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if(!(datas.get(position) instanceof IVideo))return;
        IVideo video = (IVideo) datas.get(position);
        if(video.hasVideoDetails()){
            VideoDetailsActivity.startActivity(this,video);
        }else{
            VideoPlayerActivity.startActivity(this,video);
        }
    }

    private RefreshRecyclerActivityImpl<IBangumiMoreRoot> callback = new RefreshRecyclerActivityImpl<IBangumiMoreRoot>() {

        @Override
        public void onSuccess(int enqueueKey, IBangumiMoreRoot response) {
            if(response == null || !response.isSuccess() || mListAdapter == null)return;
            List<? extends IVideo> list = response.getList();
            if(list == null || list.size() == 0){
                mListAdapter.setLoad(false);
                showToast(getString(R.string.not_more));
            }else{
                mListAdapter.setList(list,isRefresh());
                mListAdapter.setLoad(hasLoad);
            }
        }

    };
}
