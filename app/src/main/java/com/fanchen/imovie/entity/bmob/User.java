package com.fanchen.imovie.entity.bmob;

import android.text.TextUtils;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.util.DateUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by fanchen on 2017/10/8.
 */
public class User extends BmobObj {
    public static final int SEX_MAN = 0;
    public static final int SEX_WOMAN = 1;
    public static final int SEX_NON = 2;

    public static final int LEVEL_NON = 0;
    public static final int LEVEL_VIP = 1;
    public static final int LEVEL_SVIP = 2;
    public static final int LEVEL_ADMIN = -1;

    private transient static User loginUser = null;

    private String username;
    private String password;
    private String email;
    private Boolean emailVerified;
    private String sessionToken;
    private String phone;
    private Boolean phoneVerified;
    private String nickName;
    private String birthday = DateUtil.getCurrentDate("yyyy-MM-dd");
    private int sex = SEX_NON;
    private String headerUrl;
    private BmobFile header;
    private boolean authQQ;
    private boolean authWX;
    private boolean authWB;

    private int level = LEVEL_NON;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        nickName = "次元用户" + username.substring(username.length() - 3);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }


    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return TextUtils.isEmpty(nickName) ? username : nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public static void setLoginUser(User loginUser) {
        User.loginUser = loginUser;
    }

    public boolean isAuthQQ() {
        return authQQ;
    }

    public void setAuthQQ(boolean authQQ) {
        this.authQQ = authQQ;
    }

    public boolean isAuthWX() {
        return authWX;
    }

    public void setAuthWX(boolean authWX) {
        this.authWX = authWX;
    }

    public boolean isAuthWB() {
        return authWB;
    }

    public void setAuthWB(boolean authWB) {
        this.authWB = authWB;
    }

    public BmobFile getHeader() {
        return header;
    }

    public void setHeader(BmobFile header) {
        this.header = header;
    }

    public void register(OnRefreshListener l) {
        save(l);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void login(OnLoginListener l) {
        if (l == null || IMovieAppliction.app == null) return;
        final SoftReference<OnLoginListener> reference = new SoftReference<OnLoginListener>(l);
        BmobQuery<User> usernameQuery = new BmobQuery<User>();
        usernameQuery.addWhereEqualTo("username", username);
        BmobQuery<User> passwordQuery = new BmobQuery<User>();
        passwordQuery.addWhereEqualTo("password", password);
        List<BmobQuery<User>> andQuerys = new ArrayList<BmobQuery<User>>();
        andQuerys.add(usernameQuery);
        andQuerys.add(passwordQuery);
        BmobQuery<User> query = new BmobQuery<User>();
        query.and(andQuerys);
        query.include("wx,wb,qq");
        l.onStart();
        query.findObjects(IMovieAppliction.app, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                OnFindListener<User> userOnFindListener = reference.get();
                if (userOnFindListener != null) {
                    userOnFindListener.onSuccess(list);
                    userOnFindListener.onFinish();
                }
            }

            @Override
            public void onError(int i, String s) {
                OnFindListener<User> userOnFindListener = reference.get();
                if (userOnFindListener != null) {
                    userOnFindListener.onError(i, s);
                    userOnFindListener.onFinish();
                }
            }
        });
    }

    public abstract static class OnFindListener<T> extends FindListener<T> {
        public abstract void onStart();

        public abstract void onFinish();
    }

    public abstract static class OnLoginListener extends OnFindListener<User> {

        public abstract void onLoginSuccess(User user);

        @Override
        public void onSuccess(List<User> list) {
            if (list != null && list.size() > 0) {
                onLoginSuccess(list.get(0));
                save2File(list.get(0));
            } else {
                onError(-1, "登录失败");
            }
        }
    }

    /**
     * @param listener
     */
    public void update(final OnUpdateListener listener) {
        if (listener == null) return;
        super.update(new OnUpdateListener() {

            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onFinish() {
                listener.onFinish();
            }

            @Override
            public void onSuccess() {
                save2File(loginUser);
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onFailure(i, s);
            }
        });
    }

    /**
     * @param objectId
     * @param listener
     */
    public void update(String objectId, final OnUpdateListener listener) {
        if (listener == null) return;
        super.update(objectId, new OnUpdateListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onFinish() {
                listener.onFinish();
            }

            @Override
            public void onSuccess() {
                save2File(loginUser);
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onFailure(i, s);
            }
        });
    }


    public static final void logout() {
        loginUser = null;
        try {
            File filesDir = IMovieAppliction.app.getFilesDir();
            new File(filesDir, "user.obj").delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void save2File(User list) {
        ObjectOutputStream oos = null;
        try {
            File filesDir = IMovieAppliction.app.getFilesDir();
            oos = new ObjectOutputStream(new FileOutputStream(new File(filesDir, "user.obj")));
            oos.writeObject(list);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static final User getLoginUser() {
        if (loginUser != null) return loginUser;
        File filesDir = IMovieAppliction.app.getFilesDir();
        File file = new File(filesDir, "user.obj");
        if (!file.exists()) return loginUser;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            loginUser = (User) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return loginUser;
    }

}
