package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.DownloadPagerAdapter;
import com.fanchen.imovie.base.BaseTabActivity;
import com.fanchen.imovie.dialog.XiguaThunderDialog;
import com.xigua.p2p.StorageUtils;
import com.xunlei.XLAppliction;

/**
 * 下载管理   视频、应用
 * Created by fanchen on 2017/10/3.
 */
public class DownloadTabActivity extends BaseTabActivity {

    private boolean deleteMode = false;

    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, DownloadTabActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        showToast(getString(R.string.xunlei_hit));
        showDownload(getIntent().getData());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showDownload(intent.getData());
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        return new DownloadPagerAdapter(fm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                OnDeleteListernr listernr = (OnDeleteListernr) getVisibleFragment();
                if (listernr != null) listernr.setDeleteMode(deleteMode = true);
                break;
            case R.id.action_add:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                CharSequence s = clipboardManager.getText();
                if(TextUtils.isEmpty(s) || !showDownload(Uri.parse(s.toString()))){
                    XiguaThunderDialog.show(this, "新建下载");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.download);
    }

    private boolean showDownload(Uri data) {
        if (data == null || data.getScheme() == null) return false;
        String scheme = data.getScheme();
        if (scheme.equalsIgnoreCase("xg") || scheme.equalsIgnoreCase("xgadd") || scheme.equalsIgnoreCase("xgplay")) {
            XiguaThunderDialog.show(this, "新建西瓜下载", data, StorageUtils.getCachePath());
            return true;
        } else if (scheme.equalsIgnoreCase("magnet") || scheme.equalsIgnoreCase("ftp") || scheme.equalsIgnoreCase("thunder") || scheme.equalsIgnoreCase("ed2k")) {
            XiguaThunderDialog.show(this, "新建迅雷下载", data, XLAppliction.XL_PATH);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (deleteMode) {
            OnDeleteListernr listernr = (OnDeleteListernr) getVisibleFragment();
            if (listernr != null) listernr.setDeleteMode(deleteMode = false);
        } else {
            super.onBackPressed();
        }
    }

    public interface OnDeleteListernr {
        void setDeleteMode(boolean mode);
    }
}
