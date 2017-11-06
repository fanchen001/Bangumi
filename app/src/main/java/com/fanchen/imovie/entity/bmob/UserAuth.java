package com.fanchen.imovie.entity.bmob;

import android.text.TextUtils;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by fanchen on 2017/10/8.
 */
public class UserAuth extends BmobObject {

    public static final String SNS_TYPE_WEIBO = "weibo";
    public static final String SNS_TYPE_QQ = "qq";
    public static final String SNS_TYPE_WEIXIN = "weixin";

    private User user;
    private String snsType;
    private String accessToken;
    private String expiresIn;
    private String userId;

    public UserAuth() {
    }

    public UserAuth(String snsType, String accessToken, String expiresIn, String userId) {
        this.snsType = snsType;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSnsType() {
        return snsType;
    }

    public void setSnsType(String snsType) {
        this.snsType = snsType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void login(User.OnLoginListener l,Map<String,String> map) {
        if (l == null || IMovieAppliction.app == null) return;
        BmobQuery<UserAuth> usernameQuery = new BmobQuery<UserAuth>();
        usernameQuery.addWhereEqualTo("snsType", snsType);
        BmobQuery<UserAuth> passwordQuery = new BmobQuery<UserAuth>();
        passwordQuery.addWhereEqualTo("accessToken", accessToken);
        List<BmobQuery<UserAuth>> andQuerys = new ArrayList<BmobQuery<UserAuth>>();
        andQuerys.add(usernameQuery);
        andQuerys.add(passwordQuery);
        BmobQuery<UserAuth> query = new BmobQuery<UserAuth>();
        query.and(andQuerys);
        query.include("user");
        l.onStart();
        query.findObjects(IMovieAppliction.app, new AuthFindListene(l,map));
    }

    private class AuthFindListene extends FindListener<UserAuth> {

        private User.OnLoginListener onLoginListener;
        private Map<String,String> map;

        public AuthFindListene(User.OnLoginListener l,Map<String,String> map) {
            onLoginListener = l;
            this.map = map;
        }

        @Override
        public void onSuccess(List<UserAuth> list) {
            if (IMovieAppliction.app == null || onLoginListener == null) return;
            if (list != null && list.size() > 0 && list.get(0).getUser() != null) {
                User user = list.get(0).getUser();
                user.login(new AuthLoginListener(onLoginListener));
                LogUtil.e("AuthFindListene", "第三方账号已经绑定了次元番账号,直接登录...");
            } else {
                //auth没有绑定账号，新注册一个默认账号
                String userName = "user_" + System.currentTimeMillis() / 1000;
                String nickName = "用户_" + System.currentTimeMillis() / 1000;
                User user = new User(userName, "000000");
                user.setNickName(nickName);
                if (SNS_TYPE_QQ.equals(getSnsType())) {
                    user.setAuthQQ(true);
                    if(map != null){
                        String gender = map.get("gender");
                        if("男".equals(gender)){
                            user.setSex(User.SEX_MAN);
                        }else  if("女".equals(gender)){
                            user.setSex(User.SEX_WOMAN);
                        }else{
                            user.setSex(User.SEX_NON);
                        }
                        user.setHeaderUrl(map.get("profile_image_url"));
                        user.setNickName(map.get("screen_name"));
                    }
                } else if (SNS_TYPE_WEIBO.equals(getSnsType())) {
                    user.setAuthWB(true);
                    user.setNickName(TextUtils.isEmpty(map.get("screen_name") ) ? map.get("name") : map.get("screen_name"));
                    user.setHeaderUrl(TextUtils.isEmpty(map.get("profile_image_url") ) ? map.get("iconurl") : map.get("profile_image_url"));
                    String gender = map.get("gender");
                    if("男".equals(gender)){
                        user.setSex(User.SEX_MAN);
                    }else  if("女".equals(gender)){
                        user.setSex(User.SEX_WOMAN);
                    }else{
                        user.setSex(User.SEX_NON);
                    }
                } else if (SNS_TYPE_WEIXIN.equals(getSnsType())) {
                    user.setAuthWX(true);
                }
                user.save(IMovieAppliction.app,new UserSaveListener(user,onLoginListener));
            }
        }

        @Override
        public void onError(int i, String s) {
            if (IMovieAppliction.app == null || onLoginListener == null) return;
            onLoginListener.onError(i, s);
            onLoginListener.onFinish();
            LogUtil.e("AuthFindListene", "第三方账号登录直接失败...");
        }

    }

    private class UserSaveListener extends SaveListener{

        private User user;
        private User.OnLoginListener onLoginListener;

        public UserSaveListener(User user,User.OnLoginListener onLoginListener) {
            this.user = user;
            this.onLoginListener =onLoginListener;
        }

        @Override
        public void onSuccess() {
            if (IMovieAppliction.app == null || onLoginListener == null) return;
            setUser(user);
            save(IMovieAppliction.app,new AuthSaveListener(onLoginListener));
        }

        @Override
        public void onFailure(int i, String s) {
            if (IMovieAppliction.app == null || onLoginListener == null) return;
            onLoginListener.onError(i,s);
            onLoginListener.onFinish();
        }
    }

    private class AuthSaveListener extends  SaveListener {

        private User.OnLoginListener onLoginListener;

        public AuthSaveListener(User.OnLoginListener reference){
            this.onLoginListener = reference;
        }

        @Override
        public void onSuccess() {
            if (IMovieAppliction.app == null || onLoginListener == null) return;
            getUser().login(new AuthLoginListener(onLoginListener));
            LogUtil.e("AuthSaveListener", "新注册的第三方账号和次元番账号关联成功...");
        }

        @Override
        public void onFailure(int i, String s) {
            if (IMovieAppliction.app == null || onLoginListener == null) return;
            onLoginListener.onError(i,s);
            onLoginListener.onFinish();
            LogUtil.e("AuthSaveListener", "新注册的第三方账号和次元番账号关联失败...");
        }
    };

    private class AuthLoginListener extends User.OnLoginListener {
        private User.OnLoginListener onLoginListener;

        public AuthLoginListener(User.OnLoginListener reference) {
            this.onLoginListener = reference;
        }

        @Override
        public void onLoginSuccess(User user) {
            if (IMovieAppliction.app == null  || onLoginListener == null) return;
            onLoginListener.onLoginSuccess(user);
            LogUtil.e("AuthLoginListener", "第三方账号登录成功...");
        }

        @Override
        public void onStart() {
        }

        @Override
        public void onFinish() {
            if (IMovieAppliction.app == null || onLoginListener == null) return;
            onLoginListener.onFinish();
        }

        @Override
        public void onError(int i, String s) {
            if (IMovieAppliction.app == null || onLoginListener == null) return;
            onLoginListener.onError(i, s);
            LogUtil.e("AuthLoginListener", "第三方账号登录失败...");
        }
    }
}
