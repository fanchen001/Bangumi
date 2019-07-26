package com.fanchen.imovie.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.BangumiListAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerActivity;
import com.fanchen.imovie.dialog.BaseDialog;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IBangumiTitle;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.RegularUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 番剧《更多》按钮  列表显示Activity
 * * Created by fanchen on 2017/9/19.
 */
public class BangumiListActivity extends BaseRecyclerActivity {
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String ISLOAD = "load";
    public static final String PAGE_START = "page_start";
    public static final String CLASS_NAME = "className";

    private BangumiListAdapter mVideoListAdapter;
    private String url;
    private String title;
    private String className;
    private boolean isLoad;
    private boolean isSkip = false;

    /**
     *
     * @param activity
     * @param title
     * @param url
     * @param className
     */
    public static void startActivity(Activity activity, String title, String url,String className) {
        startActivity(activity, title, 1, url, className, true);
    }

    /**
     *
     * @param activity
     * @param title
     * @param pageStart
     * @param url
     * @param className
     * @param isLoad
     */
    public static void startActivity(Activity activity, String title,int pageStart, String url,String className ,boolean isLoad) {
        try {
            Intent intent = new Intent(activity, BangumiListActivity.class);
            intent.putExtra(URL, url);
            intent.putExtra(TITLE, title);
            intent.putExtra(PAGE_START, pageStart);
            intent.putExtra(ISLOAD, isLoad);
            intent.putExtra(CLASS_NAME, className);
            activity.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param activity
     * @param title
     * @param url
     * @param className
     * @param isLoad
     */
    public static void startActivity(Activity activity, String title,String url,String className ,boolean isLoad) {
        startActivity(activity, title, 1, url, className, isLoad);
    }

    /**
     *
     * @param activity
     * @param bangumiTitle
     * @param isLoad
     */
    public static void startActivity(Activity activity,IBangumiTitle bangumiTitle,boolean isLoad) {
        startActivity(activity,bangumiTitle.getTitle(),bangumiTitle.getStartPage(),bangumiTitle.getId(),bangumiTitle.getServiceClassName(),isLoad);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        url = getIntent().getStringExtra(URL);
        title = getIntent().getStringExtra(TITLE);
        isLoad = getIntent().getBooleanExtra(ISLOAD, false);
        className = getIntent().getStringExtra(CLASS_NAME);
        setPageStart(getIntent().getIntExtra(PAGE_START, 1));
        super.initActivity(savedState, inflater);
    }

    @Override
    protected boolean hasLoad() {
        return true;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(this, BaseAdapter.LinearLayoutManagerWrapper.VERTICAL, false);
    }

    @Override
    protected BaseAdapter getAdapter(Picasso picasso) {
        return mVideoListAdapter = new BangumiListAdapter(this, picasso);
    }

    @Override
    protected void loadData(RetrofitManager retrofit, int page) {
        LogUtil.e("BangumiListActivity","url -> " + url);
        LogUtil.e("BangumiListActivity","page -> " + page);
        retrofit.enqueue(className, callback, "more", url, Integer.valueOf(page));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_skip,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_skip:
                DialogUtil.showInputDialog(this,inputListener);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getActivityTitle() {
        return title;
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if(!(datas.get(position) instanceof IVideo))return;
        IVideo video = (IVideo) datas.get(position);
        VideoDetailsActivity.startActivity(this,video);
    }

    private RefreshRecyclerActivityImpl<IBangumiMoreRoot> callback = new RefreshRecyclerActivityImpl<IBangumiMoreRoot>() {

        @Override
        public void onSuccess(int enqueueKey, IBangumiMoreRoot response) {
            if (response == null || !response.isSuccess() || mVideoListAdapter == null) return;
            mVideoListAdapter.setList(response.getList(),isRefresh() || BangumiListActivity.this.isSkip);
            BangumiListActivity.this.isSkip = false;
            mVideoListAdapter.setLoad(isLoad);
            mVideoListAdapter.setLoading(false);
        }

    };

    private DialogUtil.OnInputListener inputListener = new DialogUtil.OnInputListener() {

        @Override
        public void onInput(EditText editText, BaseDialog<?> dialog) {
            String string = getEditTextString(editText);
            if(RegularUtil.isAllNumric(string)){
                BangumiListActivity.this.isSkip = true;
                setPage(Integer.valueOf(string));
                loadData(getRetrofitManager(),getPage());
            }
            dialog.dismiss();
        }

    };
}
