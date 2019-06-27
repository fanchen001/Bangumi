package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.entity.bmob.User;
import com.fanchen.imovie.entity.bmob.UserAuth;
import com.fanchen.imovie.entity.bmob.VideoCollect;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.KeyBoardUtils;
import com.fanchen.imovie.util.LogUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 用户登录
 * Created by fanchen on 2017/8/25.
 */
public class LoginActivity extends BaseToolbarActivity implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {
    @InjectView(R.id.iv_login_eyes)
    protected ImageView mEyeImageView;
    @InjectView(R.id.et_login_username)
    protected EditText mUsernameEditText;
    @InjectView(R.id.et_login_password)
    protected EditText mPasswordEditText;
    @InjectView(R.id.btn_password_visible)
    protected ImageButton mVisibleImageButton;
    @InjectView(R.id.tv_forgetpassword)
    protected TextView mForgetTextView;
    @InjectView(R.id.tv_registeraccount)
    protected TextView mRegisterTextView;
    @InjectView(R.id.btn_login)
    protected Button mLoginButton;
    @InjectView(R.id.ll_wechat_logo)
    protected LinearLayout mWechatLinearLayout;
    @InjectView(R.id.ll_tencent_logo)
    protected LinearLayout mTencentLinearLayout;
    @InjectView(R.id.ll_sina_logo)
    protected LinearLayout mSinaLinearLayout;

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.login_user);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mVisibleImageButton.setOnClickListener(this);
        mForgetTextView.setOnClickListener(this);
        mRegisterTextView.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mWechatLinearLayout.setOnClickListener(this);
        mTencentLinearLayout.setOnClickListener(this);
        mSinaLinearLayout.setOnClickListener(this);
        mUsernameEditText.setOnFocusChangeListener(this);
        mUsernameEditText.addTextChangedListener(this);
        mPasswordEditText.setOnFocusChangeListener(this);
        mPasswordEditText.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_registeraccount:
                RegisterActivity.startActivity(this);
                break;
            case R.id.btn_password_visible:
                int inputType = mPasswordEditText.getInputType();
                if (inputType == InputType.TYPE_CLASS_TEXT) {
                    mVisibleImageButton.setImageResource(R.drawable.login_pss_invisable);
                    mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    mVisibleImageButton.setImageResource(R.drawable.login_pass_visable);
                    mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                break;
            case R.id.tv_forgetpassword:
                FindPassActivity.startActivity(this);
                break;
            case R.id.btn_login:
                KeyBoardUtils.closeKeyboard(this,mUsernameEditText);
                KeyBoardUtils.closeKeyboard(this,mPasswordEditText);
                String user = getEditTextString(mUsernameEditText);
                String pass = getEditTextString(mPasswordEditText);
                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                    showSnackbar(getString(R.string.login_hit));
                    return;
                }
                if (user.length() < 4 || pass.length() < 6) {
                    showSnackbar(getString(R.string.login_lenght_hit));
                    return;
                }
                new User(user,pass).login(loginListener);
                break;
            case R.id.ll_wechat_logo:
                if(UMShareAPI.get(this).isInstall(this,SHARE_MEDIA.WEIXIN)){
                    UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, authListener);
                }else{
                    showSnackbar(getString(R.string.apk_not_install));
                }
                break;
            case R.id.ll_tencent_logo:
                if(UMShareAPI.get(this).isInstall(this,SHARE_MEDIA.QQ)){
                    UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.QQ, authListener);
                }else{
                    showSnackbar(getString(R.string.apk_not_install));
                }
                break;
            case R.id.ll_sina_logo:
                if(UMShareAPI.get(this).isInstall(this,SHARE_MEDIA.SINA)){
                    UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.SINA, authListener);
                }else{
                    showSnackbar(getString(R.string.apk_not_install));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (v.getId() == R.id.et_login_username) {
                mEyeImageView.setImageResource(R.drawable.login_icon_eyes_open);
            } else if (v.getId() == R.id.et_login_password) {
                mEyeImageView.setImageResource(R.drawable.login_icon_eyes_closed);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(getEditTextString(mPasswordEditText)) && !TextUtils.isEmpty(getEditTextString(mUsernameEditText))) {
            mLoginButton.setEnabled(true);
            mLoginButton.setClickable(true);
        } else {
            mLoginButton.setClickable(false);
            mLoginButton.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void synchronizationVideo(User user){
        if(IMovieAppliction.app == null || user == null)return;
        BmobQuery<VideoCollect> query = new BmobQuery<>();
        query.addWhereEqualTo("userId",user.getObjectId());
        query.findObjects(IMovieAppliction.app,findVideoListener);
    }

    private UMAuthListener authListener = new UMAuthListener() {

        @Override
        public void onStart(SHARE_MEDIA share_media) {
            showSnackbar(getString(R.string.login_start));
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            UserAuth auth = null;
            if(SHARE_MEDIA.QQ == share_media){
                auth = new UserAuth(UserAuth.SNS_TYPE_QQ,map.get("accessToken"),map.get("expires_in"),map.get("openid"));
            }else if(SHARE_MEDIA.WEIXIN == share_media){
                auth = new UserAuth(UserAuth.SNS_TYPE_WEIXIN,map.get("accessToken"),map.get("expires_in"),map.get("openid"));
            }else if(SHARE_MEDIA.SINA == share_media){
                auth = new UserAuth(UserAuth.SNS_TYPE_WEIBO,map.get("accessToken"),map.get("expires_in"),map.get("uid"));
            }
            if(auth != null) auth.login(loginListener,map);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showSnackbar(getString(R.string.login_error));
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            showSnackbar(getString(R.string.login_cancel));
        }

    };

    private User.OnLoginListener loginListener = new User.OnLoginListener() {

        @Override
        public void onStart() {
            DialogUtil.showProgressDialog(LoginActivity.this,getString(R.string.login_ing));
        }

        @Override
        public void onFinish() {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onLoginSuccess(User user) {
            synchronizationVideo(user);
            //发布登录事件
            postAppEvent(new AppEvent(LoginActivity.class, AppEvent.LOGIN, user));
            showToast(getString(R.string.login_success));
            finish();
        }

        @Override
        public void onError(int i, String s) {
            showSnackbar(s);
        }

    };

    private FindListener<VideoCollect> findVideoListener = new FindListener<VideoCollect>() {

        @Override
        public void onSuccess(List<VideoCollect> list) {
            LogUtil.e(LoginActivity.class,"同步video成功");
            AsyTaskQueue.newInstance().execute(new SaveTaskListener(list));
        }

        @Override
        public void onError(int i, String s) {
            LogUtil.e(LoginActivity.class,"同步video失败");
        }

    };

    private class SaveTaskListener extends AsyTaskListenerImpl<Integer>{

        private List<VideoCollect> list;

        public SaveTaskListener(List<VideoCollect> list) {
            this.list = list;
        }

        @Override
        public Integer onTaskBackground() {
            if(getLiteOrm() == null || list == null)return 0;
            return getLiteOrm().insert(list);
        }

    };
}
