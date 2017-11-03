package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.entity.bmob.User;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;

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
    @InjectView(R.id.user_profile_sex_male)
    RadioButton mSexMaleRadioButton;
    @InjectView(R.id.user_profile_sex_female)
    RadioButton mSexFemaleRadioButton;
    @InjectView(R.id.user_profile_btn_save)
    Button mSaveButton;

    /**
     *
     * @param context
     */
    public static void startActivity(Context context){
        Intent intent = new Intent(context,UserInfoActivity.class);
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
        mSaveButton.setOnClickListener(this);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        User loginUser = getLoginUser();
        if(loginUser == null){
            finish();
            return;
        }
        if(loginUser.getSex() == User.SEX_WOMAN){
            mSexMaleRadioButton.setChecked(true);
        }else{
            mSexFemaleRadioButton.setChecked(true);
        }
        mNicknameEditText.setText(loginUser.getNickName());
        mEmailEditText.setText(loginUser.getEmail());
        mBirthTextView.setText(loginUser.getBirthday());
        if (!TextUtils.isEmpty(loginUser.getHeaderUrl())) {
            Picasso.with(this).load(loginUser.getHeaderUrl()).into(mIconsetImageView);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
