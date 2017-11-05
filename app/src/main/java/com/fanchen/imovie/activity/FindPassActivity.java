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
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.entity.bmob.User;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.KeyBoardUtils;
import com.fanchen.imovie.util.SmsUtil;
import com.fanchen.imovie.view.VerificationButton;

import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.smssdk.SMSSDK;

/**
 * 找回密码
 * Created by fanchen on 2017/8/27.
 */
public class FindPassActivity extends BaseToolbarActivity implements TextWatcher, View.OnClickListener {
    @InjectView(R.id.et_phonenumber)
    protected EditText mPhoneEditText;
    @InjectView(R.id.find_password_delete)
    protected ImageView mDeleteImageView;
    @InjectView(R.id.et_verification)
    protected EditText mVerificationEditText;
    @InjectView(R.id.btn_sendmessage)
    protected VerificationButton mMessageButton;
    @InjectView(R.id.tv_cannotverification)
    protected TextView mCannotTextView;
    @InjectView(R.id.btn_next)
    protected Button mNextButton;

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FindPassActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmsUtil.register();
        registerReceiver(msgReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SmsUtil.unRegister();
        unregisterReceiver(msgReceiver);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_findpass;
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.find_pass);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mDeleteImageView.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mCannotTextView.setOnClickListener(this);
        mMessageButton.setOnClickListener(this);
        mPhoneEditText.addTextChangedListener(this);
        mVerificationEditText.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_password_delete:
                mPhoneEditText.setText(null);
                break;
            case R.id.btn_sendmessage:
                KeyBoardUtils.closeKeyboard(this, mPhoneEditText);
                KeyBoardUtils.closeKeyboard(this, mVerificationEditText);
                String phone = getEditTextString(mPhoneEditText);
                if (!TextUtils.isEmpty(phone) && phone.length() == 11 && phone.startsWith("1")) {
                    //获取验证码
                    SmsUtil.getVerificationCode(phone, msmListener);
                } else {
                    mMessageButton.setStopDown(true);
                    showSnackbar(getString(R.string.error_phone));
                }
                break;
            case R.id.tv_cannotverification:
                DialogUtil.showBottomCancleDialog(this);
                break;
            case R.id.btn_next:
                KeyBoardUtils.closeKeyboard(this, mPhoneEditText);
                KeyBoardUtils.closeKeyboard(this, mVerificationEditText);
                String phoneNamber = getEditTextString(mPhoneEditText);
                String verification = getEditTextString(mVerificationEditText);
                if (TextUtils.isEmpty(phoneNamber) || TextUtils.isEmpty(verification)) {
                    showSnackbar(getString(R.string.error_null));
                    return;
                }
                SmsUtil.submitVerificationCode(phoneNamber, verification);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(getEditTextString(mPhoneEditText)) && !TextUtils.isEmpty(getEditTextString(mVerificationEditText))) {
            mNextButton.setEnabled(true);
            mNextButton.setClickable(true);
        } else {
            mNextButton.setEnabled(false);
            mNextButton.setClickable(false);
        }
        if (!TextUtils.isEmpty(getEditTextString(mPhoneEditText))) {
            mDeleteImageView.setVisibility(View.VISIBLE);
        } else {
            mDeleteImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

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

    private SmsUtil.OnMsmListener msmListener = new SmsUtil.OnMsmListener() {

        @Override
        public void onSuccess(int event) {
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                // 验证成功回调
                String phone = getEditTextString(mPhoneEditText);
                BmobQuery<User> query = new BmobQuery<>();
                query.addWhereEqualTo("phone", phone);
                findListener.onStart();
                query.findObjects(IMovieAppliction.app, findListener);
            } else {
                showSnackbar(getString(R.string.get_code_success));
            }
        }

        @Override
        public void onFinal(Throwable e) {
            showSnackbar(e.toString());
        }

    };

    private FindListener<User> findListener = new User.OnFindListener<User>() {

        @Override
        public void onStart() {
            DialogUtil.showProgressDialog(FindPassActivity.this, getString(R.string.find_pass_ing));
        }

        @Override
        public void onFinish() {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onSuccess(List<User> list) {
            if (list != null && list.size() > 0 && !isFinishing()) {
                DialogUtil.showMessageDialog(FindPassActivity.this, getString(R.string.your_pwd) + list.get(0).getPassword());
            } else {
                showSnackbar(getString(R.string.find_pass_error));
            }
        }

        @Override
        public void onError(int i, String s) {
            showSnackbar(s);
        }

    };
}
