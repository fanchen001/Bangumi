package com.fanchen.imovie.fragment;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadReceiver;
import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.activity.WebActivity;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.entity.biliplus.BiliplusPlayUrl;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.service.BiliplusService;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.RegularUtil;

import java.io.File;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by fanchen on 2017/10/11.
 */
public class BiliplusFragment extends BaseFragment implements View.OnClickListener {

    @InjectView(R.id.ed_biliplus)
    EditText mPlusEditText;
    @InjectView(R.id.btn_get_info)
    Button mInfoButton;
    @InjectView(R.id.btn_html5_paly)
    Button mHtml5Button;
    @InjectView(R.id.btn_download)
    Button mDownloadButton;

    public static Fragment newInstance() {
        return new BiliplusFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_biliplus;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mInfoButton.setOnClickListener(this);
        mHtml5Button.setOnClickListener(this);
        mDownloadButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String url = getEditTextString(mPlusEditText);
        if (!TextUtils.isEmpty(url) && (RegularUtil.isAllNumric(url) || url.startsWith("http") || url.startsWith("https"))) {
            String[] split = url.split("/");
            String aid = split[split.length - 1].replace("av", "");
            switch (v.getId()) {
                case R.id.btn_get_info:
                    VideoDetailsActivity.startActivity(activity, aid, BiliplusService.class.getName());
                    break;
                case R.id.btn_download:
                    getRetrofitManager().enqueue(BiliplusService.class, callback, "playUrl", new Object[]{aid});
                    break;
                case R.id.btn_html5_paly:
                    WebActivity.startActivity(activity, String.format("https://www.biliplus.com/video/av%s", aid));
                    break;
            }
        } else {
            showSnackbar(getString(R.string.av_error_hit));
        }
    }

    private RefreshCallback<IPlayUrls> callback = new RefreshCallback<IPlayUrls>() {

        @Override
        public void onStart(int enqueueKey) {
            DialogUtil.showProgressDialog(activity, getString(R.string.loading));
        }

        @Override
        public void onFinish(int enqueueKey) {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            showSnackbar(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, IPlayUrls response) {
            if (response.isSuccess() && response instanceof BiliplusPlayUrl) {
                Map<String, String> urls = response.getUrls();
                DialogUtil.showMaterialListDialog(activity, getString(R.string.plase_select), urls.keySet(), new OnListListener(urls));
            } else {
                showSnackbar(getString(R.string.get_video_info_error));
            }
        }

    };

    private class OnListListener implements AdapterView.OnItemClickListener {

        private Map<String, String> valus;

        private OnListListener(Map<String, String> valus) {
            this.valus = valus;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Object key = parent.getItemAtPosition(position);
            String s = valus.get(key);
            File dir = new File(Environment.getExternalStorageDirectory() + "/android/data/" + activity.getPackageName() + "/download/video/");
            if (!dir.exists()) dir.mkdirs();
            if (!TextUtils.isEmpty(s) && !getDownloadReceiver().taskExists(s)) {
                getDownloadReceiver().load(s).setDownloadPath(dir + "/" + key + "_" + System.currentTimeMillis() + ".mp4").start();
                showSnackbar(getString(R.string.download_add));
            } else {
                showSnackbar(getString(R.string.task_exists));
            }
        }

    }
}
