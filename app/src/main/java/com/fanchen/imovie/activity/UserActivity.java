package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.entity.bmob.User;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.view.CircleImageView;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;

/**
 * Created by fanchen on 2017/10/11.
 */
public class UserActivity extends BaseToolbarActivity implements View.OnClickListener {
    @InjectView(R.id.iv_user_iconset)
    CircleImageView mIconsetImageView;
    @InjectView(R.id.tv_user_username)
    TextView mNameTextView;
    @InjectView(R.id.btn_user_birthday)
    Button mBirthdayButton;
    @InjectView(R.id.rl_user_profile)
    RelativeLayout mProfileRelativeLayout;
    @InjectView(R.id.rl_user_bindphone)
    RelativeLayout mPhoneRelativeLayout;
    @InjectView(R.id.iv_bindaccount_sina)
    ImageView mSinaRelativeLayout;
    @InjectView(R.id.iv_bindaccount_tencent)
    ImageView mTencentRelativeLayout;
    @InjectView(R.id.iv_bindaccount_weixin)
    ImageView mWeixinImageView;
    @InjectView(R.id.rl_user_changepassword)
    RelativeLayout mChangeRelativeLayout;
    @InjectView(R.id.tv_user_logout)
    TextView mLogoutTextView;

    /**
     *
     * @param context
     */
    public static void startActivity(Context context){
        Intent intent = new Intent(context,UserActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_user_space;
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        if(getLoginUser() == null){
            finish();
            return;
        }
        User loginUser = getLoginUser();
        mNameTextView.setText(loginUser.getNickName());
        mBirthdayButton.setText(loginUser.getBirthday());
        if(loginUser.isAuthQQ()){
            mTencentRelativeLayout.setSelected(true);
        }else if(loginUser.isAuthWB()){
            mSinaRelativeLayout.setSelected(true);
        }else if(loginUser.isAuthWX()){
            mWeixinImageView.setSelected(true);
        }
        if(!TextUtils.isEmpty(loginUser.getHeaderUrl()) && appliction != null){
            new PicassoWrap(Picasso.with(appliction)).loadVertical(loginUser.getHeaderUrl(), mIconsetImageView);
        }else if(loginUser.getHeader() != null && appliction != null){
            new PicassoWrap(Picasso.with(appliction)).loadVertical(loginUser.getHeader().getFileUrl(appliction),mIconsetImageView);
        }
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.my_space);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mIconsetImageView.setOnClickListener(this);
        mPhoneRelativeLayout.setOnClickListener(this);
        mProfileRelativeLayout.setOnClickListener(this);
        mChangeRelativeLayout.setOnClickListener(this);
        mProfileRelativeLayout.setOnClickListener(this);
        mLogoutTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_user_iconset:
                break;
            case R.id.rl_user_profile:
                UserInfoActivity.startActivity(this);
                break;
            case R.id.rl_user_bindphone:
                if(getLoginUser() != null){
                    if( !TextUtils.isEmpty(getLoginUser().getPhone())){
                        showSnackbar(getString(R.string.error_bind));
                    }else{
                        BindPhoneActivity.startActivity(this);
                    }
                }
                break;
            case R.id.rl_user_changepassword:
                break;
            case R.id.tv_user_logout:
                DialogUtil.showMaterialDialog(this, getString(R.string.logout_hit), new OnButtonClickListener() {
                    @Override
                    public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
                        dialog.dismiss();
                        if(btn == OnButtonClickListener.RIGHT){
                            User.logout();
                            postAppEvent(new AppEvent(UserActivity.class,AppEvent.LOGOUT));
                            finish();
                        }
                    }
                });
                break;
        }
    }
}
