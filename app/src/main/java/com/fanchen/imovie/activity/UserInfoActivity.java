package com.fanchen.imovie.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.entity.bmob.User;
import com.fanchen.imovie.util.DateUtil;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.LogUtil;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import butterknife.InjectView;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by fanchen on 2017/10/31.
 */
public class UserInfoActivity extends BaseToolbarActivity implements View.OnClickListener {

    @InjectView(R.id.user_profile_iconset_iv)
    ImageView mIconsetImageView;
    @InjectView(R.id.user_profile_nickname_et)
    EditText mNicknameEditText;
    @InjectView(R.id.user_profile_email_et)
    EditText mEmailEditText;
    @InjectView(R.id.user_profile_birth_textview)
    TextView mBirthTextView;
    @InjectView(R.id.user_profile_sex_rg)
    RadioGroup mRadioGroup;
    @InjectView(R.id.user_profile_btn_save)
    Button mSaveButton;

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected String getActivityTitle() {
        return getLoginUser() == null ? "" : getLoginUser().getNickName();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_info;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mBirthTextView.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        User loginUser = getLoginUser();
        if (loginUser == null) {
            finish();
            return;
        }
        if (loginUser.getSex() == User.SEX_WOMAN) {
            mRadioGroup.check(R.id.user_profile_sex_female);
        } else {
            mRadioGroup.check(R.id.user_profile_sex_male);
        }
        mNicknameEditText.setText(loginUser.getNickName());
        mEmailEditText.setText(loginUser.getEmail());
        mBirthTextView.setText(loginUser.getBirthday());
        if (!TextUtils.isEmpty(loginUser.getHeaderUrl())) {
            getPicasso().load(loginUser.getHeaderUrl()).into(mIconsetImageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_profile_birth_textview:
                Calendar ca = Calendar.getInstance();
                ca.setTime(DateUtil.getDateByFormat(getLoginUser().getBirthday(), "yyyy-MM-dd"));
                new DatePickerDialog(this, onDateSetListener, ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.user_profile_btn_save:
                String nickName = getEditTextString(mNicknameEditText);
                String email = getEditTextString(mEmailEditText);
                String birthday = getTextViewString(mBirthTextView);
                if(nickName.length() < 4){
                    showSnackbar(getString(R.string.name_4));
                    return;
                }else if(TextUtils.isEmpty(birthday)){
                    showSnackbar(getString(R.string.brithday_null));
                    return;
                }
                User loginUser = getLoginUser();
                loginUser.setNickName(nickName);
                loginUser.setBirthday(birthday);
                loginUser.setEmail(email);
                loginUser.setSex(mRadioGroup.getCheckedRadioButtonId() == R.id.user_profile_sex_male ? User.SEX_MAN : User.SEX_WOMAN);
                loginUser.update(updateListener);
                break;
        }
    }

    /**
     * 日期选择器对话框监听
     */
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String days;
            if (monthOfYear + 1 < 10) {
                if (dayOfMonth < 10) {
                    days = new StringBuffer().append(year).append("年").append("0").append(monthOfYear + 1).append("月").append("0").append(dayOfMonth).append("日").toString();
                } else {
                    days = new StringBuffer().append(year).append("年").append("0").append(monthOfYear + 1).append("月").append(dayOfMonth).append("日").toString();
                }
            } else {
                if (dayOfMonth < 10) {
                    days = new StringBuffer().append(year).append("年").append(monthOfYear + 1).append("月").append("0").append(dayOfMonth).append("日").toString();
                } else {
                    days = new StringBuffer().append(year).append("年").append(monthOfYear + 1).append("月").append(dayOfMonth).append("日").toString();
                }
            }
            mBirthTextView.setText(days);
        }
    };

    private User.OnUpdateListener updateListener = new User.OnUpdateListener() {

        @Override
        public void onStart() {
            DialogUtil.showProgressDialog(UserInfoActivity.this,getString(R.string.loading));
        }

        @Override
        public void onFinish() {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onSuccess() {
            mTitleView.setText(getLoginUser().getNickName());
            postAppEvent(new AppEvent(AppEvent.UPDATE));
            showSnackbar(getString(R.string.success));
        }

        @Override
        public void onFailure(int i, String s) {
            showSnackbar(s);
        }

    };
}
