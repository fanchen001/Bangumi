package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.KeyBoardUtils;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 用户注册
 * Created by fanchen on 2017/8/26.
 */
public class RegisterActivity extends BaseToolbarActivity implements View.OnClickListener, TextWatcher {

    private EditText mPhoneEditText;
    private ImageView mDeleteImageView;
    private Button mNextButton;
    private TextView mForgetTextView;

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
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
    protected int getLayout() {
        return R.layout.activity_register;
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.register_user);
    }

    @Override
    protected void findView(View view) {
        super.findView(view);
        mPhoneEditText = (EditText) findViewById(R.id.et_phonenumber);
        mDeleteImageView = (ImageView) findViewById(R.id.img_delete);
        mNextButton = (Button) findViewById(R.id.btn_next);
        mForgetTextView = (TextView) findViewById(R.id.tv_forgetpassword);
    }


    @Override
    protected void setListener() {
        super.setListener();
        mNextButton.setOnClickListener(this);
        mDeleteImageView.setOnClickListener(this);
        mForgetTextView.setOnClickListener(this);
        mPhoneEditText.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_delete:
                mPhoneEditText.setText(null);
                break;
            case R.id.btn_next:
                String phone = getEditTextString(mPhoneEditText);
                KeyBoardUtils.closeKeyboard(this, mPhoneEditText);
                if(!TextUtils.isEmpty(phone) && phone.length() == 11 && phone.startsWith("1")){
                    InputCodeActivity.startActivity(this,phone);
                }else{
                    showSnackbar(getString(R.string.error_phone));
                }
                break;
            case R.id.tv_forgetpassword:
                DialogUtil.showBottomAgreementDialog(this);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null) return;
        mNextButton.setEnabled(false);
        mNextButton.setClickable(false);
        mDeleteImageView.setVisibility(View.GONE);
        if (s.length() > 1) {
            if (s.length() == 11) {
                mNextButton.setEnabled(true);
                mNextButton.setClickable(true);
            } else {
                mNextButton.setClickable(false);
                mNextButton.setEnabled(false);
            }
            mDeleteImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
