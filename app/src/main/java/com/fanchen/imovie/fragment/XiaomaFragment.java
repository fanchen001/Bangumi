package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.WebActivity;
import com.fanchen.imovie.adapter.XiaomaAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.entity.xiaoma.XiaomaIndex;
import com.fanchen.imovie.entity.xiaoma.XiaomaSearch;
import com.fanchen.imovie.entity.xiaoma.XiaomaSearchResult;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.service.XiaomaService;
import com.fanchen.imovie.util.DeviceUtil;
import com.fanchen.imovie.util.KeyBoardUtils;
import com.fanchen.imovie.view.MaterialProgressBar;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by fanchen on 2017/11/4.
 */
public class XiaomaFragment extends BaseFragment implements View.OnClickListener, BaseAdapter.OnItemClickListener {

    @InjectView(R.id.ed_search_word)
    EditText mWordEditText;
    @InjectView(R.id.btn_search)
    Button mSearchButton;
    @InjectView(R.id.rlv_magnet_result)
    RecyclerView mResultListView;
    @InjectView(R.id.progressbar_search)
    MaterialProgressBar mProgressView;

    private XiaomaAdapter mXiaomaAdapter;

    public static Fragment newInstance() {
        return new XiaomaFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_recycler_search;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mXiaomaAdapter.setOnItemClickListener(this);
        mSearchButton.setOnClickListener(this);
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        mResultListView.setLayoutManager(new LinearLayoutManager(activity));
        mXiaomaAdapter = new XiaomaAdapter(activity, getPicasso());
        mResultListView.setAdapter(mXiaomaAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                String word = getEditTextString(mWordEditText);
                if (!TextUtils.isEmpty(word)) {
                    if (mProgressView.getVisibility() == View.GONE)
                        getRetrofitManager().enqueue(XiaomaService.class, callback, "search", word, DeviceUtil.getDeviceId(activity));
                    KeyBoardUtils.closeKeyboard(activity, mWordEditText);
                } else {
                    showSnackbar(getString(R.string.word_notnull));
                }
                break;
        }
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if(!(datas.get(position) instanceof XiaomaSearch))return;
        XiaomaSearch search = (XiaomaSearch) datas.get(position);
        WebActivity.startActivity(activity,search.getTitle(),search.getLink());
    }

    private RefreshCallback<XiaomaIndex<XiaomaSearchResult>> callback = new RefreshCallback<XiaomaIndex<XiaomaSearchResult>>() {

        @Override
        public void onStart(int enqueueKey) {
            if (mProgressView == null) return;
            mProgressView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish(int enqueueKey) {
            if (mProgressView == null) return;
            mProgressView.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            showSnackbar(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, XiaomaIndex<XiaomaSearchResult> response) {
            if (response == null || response.getResult() == null || mXiaomaAdapter == null) return;
            mXiaomaAdapter.clear();
            mXiaomaAdapter.addAll(response.getResult().getList());
        }

    };


}
