package com.fanchen.imovie.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.KeyBoardUtils;
import com.fanchen.imovie.util.SmsUtil;
import com.fanchen.imovie.view.VerificationButton;

import butterknife.InjectView;
import cn.smssdk.SMSSDK;


/**
 * 注册时输入验证吗页面
 * Created by fanchen on 2017/8/26.
 */
public class InputCodeActivity extends BaseToolbarActivity implements View.OnClickListener, TextWatcher {

    public static final String PHONE = "phone";

    @InjectView(R.id.et_verification)
    protected EditText mVerificationEditText;
    @InjectView(R.id.btn_sendmessage)
    protected VerificationButton mSendButton;
    @InjectView(R.id.tv_cannotverification)
    protected TextView mCannotTextView;
    @InjectView(R.id.btn_next)
    protected Button mNextButton;

    /**
     * @param context
     */
    public static void startActivity(Context context, String phone) {
        Intent intent = new Intent(context, InputCodeActivity.class);
        intent.putExtra(PHONE, phone);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmsUtil.register();
        registerReceiver(msgReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_inputcode;
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.input_code);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mVerificationEditText.addTextChangedListener(this);
        mSendButton.setOnClickListener(this);
        mCannotTextView.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    public void onMainEvent(AppEvent event) {
        if (event.what == AppEvent.REGISTER_SUCCESS && SetPwdActivity.class.getName().equals(event.from)) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
        SmsUtil.unRegister();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sendmessage:
                //获取验证码
                SmsUtil.getVerificationCode(getIntent().getStringExtra(PHONE), msmListener);
                break;
            case R.id.tv_cannotverification:
                DialogUtil.showBottomCancleDialog(this);
                break;
            case R.id.btn_next:
                KeyBoardUtils.closeKeyboard(this, mVerificationEditText);
                String verification = getEditTextString(mVerificationEditText);
                if (!TextUtils.isEmpty(verification)) {
                    SmsUtil.submitVerificationCode(getIntent().getStringExtra(PHONE), verification);
                } else {
                    showSnackbar(getString(R.string.non_verification));
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() >= 4) {
            mNextButton.setEnabled(true);
            mNextButton.setClickable(true);
        } else {
            mNextButton.setEnabled(false);
            mNextButton.setClickable(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private SmsUtil.OnMsmListener msmListener = new SmsUtil.OnMsmListener() {

        @Override
        public void onSuccess(int event) {
             // 提交验证码成功
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                // 验证成功回调
                SetPwdActivity.startActivity(InputCodeActivity.this, getIntent().getStringExtra(PHONE));
            } else {
                showSnackbar(getString(R.string.get_code_success));
            }
        }

        @Override
        public void onFinal(Throwable e) {
            showSnackbar(e.toString());
        }

    };

    /**
     * 短信广播,自动填充验证码
     *
     * @author fanchen
     */
    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {

        public final static String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getExtras() == null) return;
            // 短信广播
            if (intent.getAction().equals(SMS_ACTION)) {
                // 获取拦截到的短信数据
                String extractCode = SmsUtil.extractCode(intent);
                mVerificationEditText.setText(extractCode);
            }
        }
    };

}
