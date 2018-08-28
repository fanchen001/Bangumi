package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.preference.Preference;
import android.util.Log;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListener;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.FileUtil;
import com.fanchen.imovie.util.ShareUtil;
import com.umeng.socialize.UMShareAPI;

import java.io.File;


/**
 * 、
 * 設置與關於
 * Created by fanchen on 2017/8/26.
 */
public class SettingsActivity extends BaseToolbarActivity {

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, SettingsActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }

    /**
     * xml中绑定的方法
     * <p/>
     * 清除缓存
     */
    public void clearCache() {
        final File dir = new File(Environment.getExternalStorageDirectory(), "/Android/data/" + getPackageName() + "/cache");
        if (dir.exists()) {
            AsyTaskQueue.newInstance().execute(new ClearTaskListener(dir));
        } else {
            showToast(getString(R.string.clear_non));
        }
    }

    /**
     * xml中绑定的方法
     * <p/>
     * 软件分享
     */
    public void share() {
        ShareUtil.share(this, getString(R.string.collect_hit), getString(R.string.collect_hit));
    }

    /**
     * xml中绑定的方法
     * <p/>
     * github
     */
    public void github() {
        WebActivity.startActivity(this, "https://github.com/fanchen001/bangumi");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.setting_about);
    }


    private class ClearTaskListener extends AsyTaskListenerImpl<Void> {

        private File dir;

        public ClearTaskListener(File dir) {
            this.dir = dir;
        }

        @Override
        public void onTaskSart() {
            DialogUtil.showProgressDialog(SettingsActivity.this,getString(R.string.clear_ing));
        }

        @Override
        public void onTaskFinish() {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public Void onTaskBackground() {
            if(dir == null || !dir.exists())return null;
            FileUtil.deleteDirectory(dir.getAbsolutePath());
            return null;
        }

        @Override
        public void onTaskSuccess(Void data) {
            showToast(getString(R.string.clear_ok));
        }

    }
}
