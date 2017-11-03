package com.fanchen.imovie.fragment;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.util.DialogUtil;
import com.xunlei.downloadlib.XLDownloadUtil;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;

import java.util.List;

import butterknife.InjectView;


/**
 * Created by fanchen on 2017/8/4.
 */
public class MagnetFragment extends BaseFragment implements View.OnClickListener {

    @InjectView(R.id.ed_search_word)
    protected EditText mSearchWordEditText;
    @InjectView(R.id.btn_search)
    protected Button mSearchButton;

    public static Fragment newInstance() {
        return new MagnetFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_magnet;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mSearchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                String word = getEditTextString(mSearchWordEditText);
                if (TextUtils.isEmpty(word)) {
                    showSnackbar("请输入FTP或者磁力链");
                    return;
                }
                if (word.startsWith("magnet")) {
                    XLDownloadUtil.addMagentTask(activity, word, magentTaskListener);
                    return;
                }
                if (!word.startsWith("thunder") && !word.startsWith("ftp://")) {
                    showToast("请输入正确的链接.");
                    return;
                }
                DialogUtil.showProgressDialog(activity,getString(R.string.loading));
                XLDownloadUtil.addThunderTask(activity, word, thunderTaskListener);
                break;
        }
    }

    private XLDownloadUtil.OnThunderTaskListener thunderTaskListener = new XLDownloadUtil.OnThunderTaskListener() {

        @Override
        public void onSuccess(String title, String url) {
            VideoPlayerActivity.startActivity(activity,url);
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onError(Throwable arg0) {
            DialogUtil.closeProgressDialog();
            showToast("解析视频文件失败");
        }

    };

    private XLDownloadUtil.OnMagentTaskListener magentTaskListener = new XLDownloadUtil.OnMagentTaskListener() {

        @Override
        public void onSuccess(TorrentFileInfo fileInfo, List<TorrentFileInfo> fileInfos, String arg2) {
            VideoPlayerActivity.startActivity(activity,fileInfo);
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onError(Throwable arg0) {
            DialogUtil.closeProgressDialog();
            showToast("解析种子文件失败");
        }

    };
}
