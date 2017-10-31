package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.WebActivity;
import com.fanchen.imovie.activity.WebPlayerActivity;
import com.fanchen.imovie.adapter.FreeVideoAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.entity.VideoWeb;
import com.fanchen.imovie.util.StreamUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by fanchen on 2017/10/11.
 */
public class FreeVideoFragment extends BaseFragment implements
        BaseAdapter.OnItemClickListener, View.OnClickListener {

    @InjectView(R.id.ed_free_video)
    EditText mVideoEditText;
    @InjectView(R.id.btn_search)
    Button mSearchButton;
    @InjectView(R.id.iv_reback)
    ImageView mRebackImageView;
    @InjectView(R.id.rlv_web_list)
    RecyclerView mWebRecyclerView;

    private FreeVideoAdapter mVideoAdapter;

    public static Fragment newInstance() {
        return new FreeVideoFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_free_video;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mSearchButton.setOnClickListener(this);
        mRebackImageView.setOnClickListener(this);
        mVideoAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        mWebRecyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        mVideoAdapter = new FreeVideoAdapter(activity, Picasso.with(activity));
        mWebRecyclerView.setAdapter(mVideoAdapter);
        try {
            String json = new String(StreamUtil.stream2bytes(activity.getAssets().open("free_video.json")));
            List<VideoWeb> list = new Gson().fromJson(json, new TypeToken<List<VideoWeb>>() {}.getType());
            mVideoAdapter.addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (position < 0 ||datas == null || datas.size() <= position || !(datas.get(position) instanceof VideoWeb)) return;
        VideoWeb video = (VideoWeb) datas.get(position);
        if (!TextUtils.isEmpty(video.getUrl())) {
            WebActivity.startActivity(activity, video.getUrl());
        }
    }

    @Override
    public void onClick(View v) {
        String url = getEditTextString(mVideoEditText);
        if(!TextUtils.isEmpty(url) && (url.startsWith("http") || url.startsWith("https"))){
            switch (v.getId()){
                case R.id.btn_search:
                    String format = String.format("https://www.ai577.com/playm3u8/index.php?type=&vid=%s", url);
                    WebPlayerActivity.startActivity(activity,format);
                    break;
                case R.id.iv_reback:
                    WebActivity.startActivity(activity,url);
                    break;
            }
        }else{
            showSnackbar(getString(R.string.url_error_hit));
        }
    }
}
