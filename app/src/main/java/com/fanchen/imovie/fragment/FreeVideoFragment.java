package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.WebActivity;
import com.fanchen.imovie.activity.WebPlayerActivity;
import com.fanchen.imovie.adapter.FreeVideoAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.entity.VideoWeb;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.StreamUtil;
import com.fanchen.sniffing.SniffingUICallback;
import com.fanchen.sniffing.SniffingVideo;
import com.fanchen.sniffing.x5.SniffingUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.smtt.sdk.TbsVideo;

import java.util.List;

import butterknife.InjectView;

/**
 * FreeVideoFragment
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
    @InjectView(R.id.sp_luxian)
    Spinner mSpinner;

    private FreeVideoAdapter mVideoAdapter;
//    private VideoUrlUtil mVideoUrlUtil;

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
//        mVideoUrlUtil = VideoUrlUtil.getInstance().init(activity);
        mWebRecyclerView.setLayoutManager(new BaseAdapter.GridLayoutManagerWrapper(activity, 3));
        mVideoAdapter = new FreeVideoAdapter(activity, getPicasso());
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
        if (!(datas.get(position) instanceof VideoWeb)) return;
        VideoWeb video = (VideoWeb) datas.get(position);
        if (!TextUtils.isEmpty(video.getUrl())) {
            WebActivity.startActivity(activity, video.getUrl());
        }
    }

    @Override
    public void onClick(View v) {
        String url = getEditTextString(mVideoEditText);
        if (!TextUtils.isEmpty(url) && (url.startsWith("http") || url.startsWith("https"))) {
            switch (v.getId()) {
                case R.id.btn_search:
                    int position = mSpinner.getSelectedItemPosition();
                    String[] luxians = WebPlayerActivity.LUXIANS;
                    if (/*mVideoUrlUtil == null || */luxians.length <= position) return;
                    String videoUrl = String.format(luxians[position], url);
                    String referer = "http://movie.vr-seesee.com/vip";
                    SniffingUtil.get().activity(activity).url(videoUrl).referer(referer).callback(new SniffingUICallback() {

                        @Override
                        public void onSniffingStart(View webView, String url) {
                            DialogUtil.showProgressDialog(activity, "正在解析视频...");
                        }


                        @Override
                        public void onSniffingSuccess(View webView, String url, List<SniffingVideo> videos) {
                            if (activity == null || videos.isEmpty()) return;
                            TbsVideo.openVideo(activity, videos.get(0).getUrl());
                        }

                        @Override
                        public void onSniffingError(View webView, String url, int errorCode) {
                            if (mSpinner == null) return;
                            int position = mSpinner.getSelectedItemPosition();
                            String referer = "http://movie.vr-seesee.com/vip";
                            WebPlayerActivity.startActivity(activity, url, referer, position);
                        }

                        @Override
                        public void onSniffingFinish(View webView, String url) {
                            DialogUtil.closeProgressDialog();
                        }

                    }).start();
//                    mVideoUrlUtil.setParserTime(5 * 1000).setOnParseListener(new FreeVideoListener(url));
//                    mVideoUrlUtil.setLoadUrl(videoUrl, referer).startParse();
                    break;
                case R.id.iv_reback:
                    WebActivity.startActivity(activity, url);
                    break;
            }
        } else {
            showSnackbar(getStringFix(R.string.url_error_hit));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SniffingUtil.get().releaseAll();
//        if (mVideoUrlUtil != null) mVideoUrlUtil.destroy();
//        mVideoUrlUtil = null;
    }

//    private class FreeVideoListener implements VideoUrlUtil.OnParseWebUrlListener {
//
//        private String url = "";
//
//        public FreeVideoListener(String url) {
//            this.url = url;
//        }
//
//        @Override
//        public void onFindUrl(String videoUrl) {
//            if (activity == null) return;
//            TbsVideo.openVideo(activity, videoUrl);
//            DialogUtil.closeProgressDialog();
//        }
//
//        @Override
//        public void onError(String errorMsg) {
//            if (mSpinner == null) return;
//            int position = mSpinner.getSelectedItemPosition();
//            String referer = "http://movie.vr-seesee.com/vip";
//            WebPlayerActivity.startActivity(activity, url, referer, position);
//            DialogUtil.closeProgressDialog();
//        }
//
//    }

}
