package com.fanchen.imovie.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.xigua.p2p.P2PManager;
import com.xigua.p2p.StorageUtils;
import com.xunlei.XLAppliction;
import com.xunlei.XLManager;
import com.xunlei.downloadlib.XLService;

/**
 * XiguaThunderDialog
 * Created by fanchen on 2018/10/16.
 */
public class XiguaThunderDialog extends MaterialDialog implements
        OnButtonClickListener, TextWatcher {

    private EditText mUrlEditText;
    private EditText mFilenameEditText;
    private EditText mPathEditText;

    private Uri uri;
    private String path = "";
    private P2PManager p2PManager = null;
    private XLManager xlManager = null;

    public static void show(Context context, String title, Uri uri, String path) {
        XiguaThunderDialog dialog = new XiguaThunderDialog(context, uri, path);
        dialog.setTitleVisble(View.VISIBLE);
        dialog.setButtonVisble(View.VISIBLE);
        dialog.title(title);
        dialog.btnNum(3);
        dialog.btnText("取消下载", "边下边播", "开始下载");
        dialog.show();
    }

    public static void show(Context context, String title) {
        show(context, title, null, null);
    }

    public XiguaThunderDialog(Context context, Uri uri, String path) {
        super(context, R.layout.dialog_new_download);
        this.p2PManager = P2PManager.getInstance();
        this.xlManager = XLManager.get(context);
        this.uri = uri;
        this.path = path;
        setButtonClickListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(XLService.GET_FILENAME);
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    public View onCreateView() {
        mUrlEditText = (EditText) this.view.findViewById(R.id.editText_new_url);
        mFilenameEditText = (EditText) this.view.findViewById(R.id.editText_new_filename);
        mPathEditText = (EditText) this.view.findViewById(R.id.editText_new_path);
        if (uri == null || path == null) mUrlEditText.addTextChangedListener(this);
        return super.onCreateView();
    }

    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();
        if (uri == null || path == null) return;
        mUrlEditText.setText(Uri.decode(uri.toString()));
        if (XLManager.isXLUrlNoHttp(uri)) {
            xlManager.getFileName(Uri.decode(uri.toString()));
        } else {
            mFilenameEditText.setText(uri.getLastPathSegment());
        }
        mPathEditText.setText(path);
    }

    @Override
    public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
        if (uri == null) {
            dialog.dismiss();
        } else {
            String decode = Uri.decode(uri.toString());
            if (P2PManager.isXiguaUrl(uri)) {
                if (btn == OnButtonClickListener.RIGHT) {
                    VideoPlayerActivity.startActivity(context, decode);
                } else if (btn == OnButtonClickListener.CENTRE) {
                    p2PManager.play(decode);
                }
            } else if (XLManager.isXLUrl(uri)) {
                if (btn == OnButtonClickListener.RIGHT) {
                    VideoPlayerActivity.startActivity(context, decode);
                } else if (btn == OnButtonClickListener.CENTRE) {
                    xlManager.addTask(decode);
                }
            } else {
                Toast.makeText(context, "不支持的类型", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        context.unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (XLService.GET_FILENAME.equals(action)) {
                mFilenameEditText.setText(intent.getStringExtra(XLService.DATA));
            }
        }

    };

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null || s.length() < 15) return;
        if (P2PManager.isXiguaUrl(s.toString())) {
            mPathEditText.setText(StorageUtils.getCachePath());
            mFilenameEditText.setText(Uri.parse(s.toString()).getLastPathSegment());
        } else if (XLManager.isXLUrl(s.toString())) {
            mPathEditText.setText(XLAppliction.XL_PATH);
            xlManager.getFileName(s.toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
