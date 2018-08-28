package com.fanchen.imovie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.SystemUtil;

import butterknife.InjectView;


/**
 * 应用关于
 * Created by fanchen on 2017/8/26.
 */
public class AboutActivity extends BaseToolbarActivity implements View.OnClickListener {
    @InjectView(R.id.tv_version)
    TextView mVersionTextView;
    @InjectView(R.id.rl_btn_web)
    RelativeLayout mMsRelativeLayout;
    @InjectView(R.id.rl_btn_qqnum)
    RelativeLayout mQQRelativeLayout;
    @InjectView(R.id.rl_btn_phonenum)
    RelativeLayout mKfRelativeLayout;
    @InjectView(R.id.rl_btn_emailadress)
    RelativeLayout mEmailRelativeLayout;

    @Override
    protected int getLayout() {
        return R.layout.activity_about;
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.about);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mMsRelativeLayout.setOnClickListener(this);
        mQQRelativeLayout.setOnClickListener(this);
        mKfRelativeLayout.setOnClickListener(this);
        mEmailRelativeLayout.setOnClickListener(this);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        mVersionTextView.setText(String.format(getString(R.string.version_format), AppUtil.getVersionName(this)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_btn_web:
                break;
            case R.id.rl_btn_qqnum:
                // 打开QQ群介绍界面(对QQ群号)
                showToast("正在打开QQ...");
                String url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=541361788&card_type=group&source=qrcode";
                SystemUtil.startThreeApp(this, url);
                break;
            case R.id.rl_btn_phonenum:
                break;
            case R.id.rl_btn_emailadress:
                try {
                    String emailmunber = ((TextView) mEmailRelativeLayout.getChildAt(1)).getText().toString();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822"); // 设置邮件格式
                    intent.putExtra(Intent.EXTRA_EMAIL, emailmunber); // 接收人
                    intent.putExtra(Intent.EXTRA_CC, emailmunber); // 抄送人
                    intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
                    intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
                    startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
                } catch (Exception e) {
                    e.printStackTrace();
                    showSnackbar(getString(R.string.activity_not_found));
                }
                break;
        }
    }

}
