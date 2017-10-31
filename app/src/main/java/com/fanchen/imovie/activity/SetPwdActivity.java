package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.entity.bmob.BmobObj;
import com.fanchen.imovie.entity.bmob.User;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.KeyBoardUtils;

import butterknife.InjectView;

/**
 * 设置密码
 * Created by fanchen on 2017/8/30.
 */
public class SetPwdActivity extends BaseToolbarActivity implements View.OnClickListener, TextWatcher {
    public static final String PHONE = "phone";
    @InjectView(R.id.tv_username)
    protected TextView mUserTextView;
    @InjectView(R.id.et_password)
    protected EditText mPwdEditText;
    @InjectView(R.id.btn_password_visible)
    protected ImageButton mEyeImageButton;
    @InjectView(R.id.btn_finish)
    protected Button mButton;

    /**
     * @param context
     */
    public static void startActivity(Context context,String phone) {
        Intent intent = new Intent(context, SetPwdActivity.class);
        intent.putExtra(PHONE,phone);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_setpwd;
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        mUserTextView.setText(getIntent().getStringExtra(PHONE));
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.set_pwd);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mPwdEditText.addTextChangedListener(this);
        mEyeImageButton.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    public void onMainEvent(AppEvent event) {
        if(event.what == AppEvent.REGISTER_SUCCESS && SetPwdActivity.class.getName().equals(event.from)){
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_finish:
                KeyBoardUtils.closeKeyboard(this, mPwdEditText);
                String pwd = getEditTextString(mPwdEditText);
                if(!TextUtils.isEmpty(pwd) && pwd.length() >= 6){
                    String phone = getIntent().getStringExtra(PHONE);
                    User user = new User(phone, pwd);
                    user.setPhone(phone);
                    user.setPhoneVerified(true);
                    user.register(registerListener);
                }else{
                    showSnackbar(getString(R.string.error_phone_pwd));
                }
                break;
            case R.id.btn_password_visible:
                int inputType = mPwdEditText.getInputType();
                if (inputType == InputType.TYPE_CLASS_TEXT) {
                    mEyeImageButton.setImageResource(R.drawable.login_pss_invisable);
                    mPwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    mEyeImageButton.setImageResource(R.drawable.login_pass_visable);
                    mPwdEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length() >= 6){
            mButton.setEnabled(true);
            mButton.setClickable(true);
        }else{
            mButton.setEnabled(false);
            mButton.setClickable(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private BmobObj.OnRefreshListener registerListener = new BmobObj.OnRefreshListener() {

        @Override
        public void onStart() {
            DialogUtil.showProgressDialog(SetPwdActivity.this,getString(R.string.register_ing));
        }

        @Override
        public void onFinish() {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onSuccess() {
            showToast(getString(R.string.register_success));
            postAppEvent(new AppEvent(SetPwdActivity.class,AppEvent.REGISTER_SUCCESS));
        }

        @Override
        public void onFailure(int i, String s) {
            showSnackbar(s);
        }

    };
}
