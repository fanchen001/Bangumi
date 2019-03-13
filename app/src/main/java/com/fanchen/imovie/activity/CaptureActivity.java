package com.fanchen.imovie.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.fanchen.imovie.R;
import com.fanchen.imovie.barcode.util.CodeUtils;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.fragment.CaptureFragment;


/**
 * 默认的二维码扫描Activity
 *
 * @author fanchen
 */
public class CaptureActivity extends BaseToolbarActivity {

    private CaptureFragment captureFragment = new CaptureFragment();

    public static void startActivity(Context context){
        try {
            Intent intent = new Intent(context,CaptureActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_capture;
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.code_scan);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasCameraPermission()) {
            initCaptureFragment();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected boolean isSwipeActivity() {
        return false;
    }


    private boolean hasCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //申请CAMERA权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RESULT_FIRST_USER);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RESULT_FIRST_USER) {
            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCaptureFragment();
            } else {
                showToast(getResources().getString(R.string.permission_tip));
                finish();
            }
        }
    }

    private void initCaptureFragment() {
        if (captureFragment != null && !captureFragment.isAdded()) {
            captureFragment.setAnalyzeCallback(analyzeCallback);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commitAllowingStateLoss();
        }
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            CaptureActivity.this.setResult(RESULT_OK, resultIntent);
            if(result.startsWith("http")){
                WebActivity.Companion.startActivity(CaptureActivity.this,result);
            }else{
                WebActivity.Companion.startActivity(CaptureActivity.this,String.format("https://www.baidu.com/s?wd=%s",result));
            }
            CaptureActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            CaptureActivity.this.setResult(RESULT_OK, resultIntent);
            CaptureActivity.this.finish();
        }
    };
}