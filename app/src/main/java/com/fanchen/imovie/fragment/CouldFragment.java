package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.CouldAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.entity.xiaobo.XiaoboRoot;
import com.fanchen.imovie.entity.xiaobo.XiaoboVodBody;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.service.XiaoboService;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.KeyBoardUtils;
import com.fanchen.imovie.util.SystemUtil;
import com.fanchen.imovie.view.MaterialProgressBar;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by fanchen on 2017/8/4.
 */
public class CouldFragment extends BaseFragment implements View.OnClickListener, BaseAdapter.OnItemClickListener, BaseAdapter.OnLoadListener {
    private String[] TITLES = new String[]{"打开连接", "复制连接"};
    @InjectView(R.id.ed_search_word)
    EditText mWordEditText;
    @InjectView(R.id.btn_search)
    Button mSearchButton;
    @InjectView(R.id.rlv_magnet_result)
    RecyclerView mResultListView;
    @InjectView(R.id.progressbar_search)
    MaterialProgressBar mProgressView;

    private int page = 1;
    private String word = "";
    private CouldAdapter mSearchAdapter;

    public static Fragment newInstance() {
        return new CouldFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_recycler_search;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mSearchAdapter.setOnLoadListener(this);
        mSearchAdapter.setOnItemClickListener(this);
        mSearchButton.setOnClickListener(this);
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        mSearchAdapter = new CouldAdapter(activity);
        mResultListView.setLayoutManager(new LinearLayoutManager(activity));
        mResultListView.setAdapter(mSearchAdapter);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (datas == null || datas.size() <= position) return;
        final XiaoboVodBody body = (XiaoboVodBody) datas.get(position);
        DialogUtil.showMaterialListDialog(activity, TITLES, new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String magnet = "magnet:?xt=urn:btih:" + body.getHash();
                switch (position) {
                    case 0:
                        SystemUtil.startThreeApp(activity, magnet);
                        break;
                    case 1:
                        showSnackbar(getString(R.string.clipboard));
                        SystemUtil.putText2Clipboard(activity, magnet);
                        break;
                }
            }

        });
    }

    @Override
    public void onLoad() {
        if (!TextUtils.isEmpty(word)) {
            getRetrofitManager().enqueue(XiaoboService.class, callback, "searchVod", word, ++page);
            KeyBoardUtils.closeKeyboard(activity, mWordEditText);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                word = getEditTextString(mWordEditText);
                if (!TextUtils.isEmpty(word)) {
                    if (mProgressView.getVisibility() == View.GONE)
                        getRetrofitManager().enqueue(XiaoboService.class, callback, "searchVod", word, page = 1);
                    KeyBoardUtils.closeKeyboard(activity, mWordEditText);
                } else {
                    showSnackbar(getString(R.string.word_notnull));
                }
                break;
        }
    }

    private RefreshCallback<XiaoboRoot> callback = new RefreshCallback<XiaoboRoot>() {

        @Override
        public void onStart(int enqueueKey) {
            if (isDetached() || !isAdded() || page > 1) return;
            mProgressView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish(int enqueueKey) {
            if (isDetached() || !isAdded()) return;
            mProgressView.setVisibility(View.GONE);
            mSearchAdapter.setLoading(false);
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            if (isDetached() || !isAdded()) return;
            showSnackbar(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, XiaoboRoot response) {
            if (isDetached() || !isAdded() || response == null || response.getBody() == null) return;
            if(page == 1)mSearchAdapter.clear();
            mSearchAdapter.addAll(response.getBody().getData());
        }

    };

}
