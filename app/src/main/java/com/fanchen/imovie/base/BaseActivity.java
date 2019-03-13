package com.fanchen.imovie.base;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.aria.core.download.DownloadReceiver;
import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.LoginActivity;
import com.fanchen.imovie.db.LiteOrmManager;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.entity.bmob.User;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.jude.swipbackhelper.SwipeBackPage;
import com.litesuits.orm.LiteOrm;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 应用中所有activity应该继承该类
 * Created by fanchen on 2017/9/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 整个应用上下文
     **/
    public IMovieAppliction appliction = IMovieAppliction.app;
    /**
     * activity 主布局
     **/
    private View mMainView;
    /****/
    private long preTime = System.currentTimeMillis();
    private LiteOrm mLiteOrm;
    private SwipeBackPage mBackPage;
    private RetrofitManager mRetrofitManager;
    private boolean isDestroy = false;
    private List<BroadcastReceiver> mBroadcastReceiver = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appliction = IMovieAppliction.app != null ? IMovieAppliction.app : (IMovieAppliction) getApplication();
        mLiteOrm = LiteOrmManager.getInstance(appliction).getLiteOrm("imovie.db");
        if (appliction != null) {
            appliction.addActivity(this);
        }
        if (isRegisterEventBus())
            EventBus.getDefault().register(this);
        SwipeBackHelper.onCreate(this);
        mBackPage = SwipeBackHelper.getCurrentPage(this);// 获取当前页面
        mBackPage.setSwipeBackEnable(isSwipeActivity());// 设置是否可滑动
        mBackPage.setSwipeEdgePercent(getEdgePercent());// 可滑动的范围。百分比。0.2表示为左边20%的屏幕
        mBackPage.setSwipeSensitivity(getSensitivity());// 对横向滑动手势的敏感程度。0为迟钝 1为敏感
        mBackPage.setClosePercent(getClosePercent());// 触发关闭Activity百分比
        mBackPage.setSwipeRelateEnable(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);// 是否与下一级activity联动(微信效果)仅限5.0以上机器
        mBackPage.setDisallowInterceptTouchEvent(false);
        if (!IMovieAppliction.isInitSdk && appliction != null) {
            appliction.mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    finish();
                }

            }, 2000);
        } else {
            isDestroy = false;
            init(savedInstanceState);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 使用滑动关闭功能
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        for (BroadcastReceiver receiver : mBroadcastReceiver) {
            try {
                super.unregisterReceiver(receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mBroadcastReceiver.clear();
        if (isRegisterEventBus())
            EventBus.getDefault().unregister(this);
        // 使用滑动关闭功能
        if (appliction != null)
            appliction.removeActivity(this);
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 防止调用onStop后使用fragment出现
        // Can not perform this action after onSaveInstanceState 异常
        onNewIntent(new Intent());
    }

    @Override
    protected void onSaveInstanceState(Bundle arg0) {
        super.onSaveInstanceState(arg0);
        // 防止调用onSaveInstanceState后使用fragment出现
        // Can not perform this action after onSaveInstanceState 异常
        onNewIntent(new Intent());
    }

    public View getMainView() {
        return mMainView;
    }

    /**
     * 是否使用eventbus
     *
     * @return
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    /**
     * 默认是否可滑动返回
     *
     * @return
     */
    protected boolean isSwipeActivity() {
        return true;
    }

    /**
     * 设置是否可滑动返回
     */
    public void setBackEnable(boolean enable) {
        if (mBackPage != null) {
            mBackPage.setSwipeBackEnable(enable);
        }
    }

    /**
     * 可滑动的范围。百分比。0.2表示为左边20%的屏幕
     *
     * @return
     */
    protected float getEdgePercent() {
        return 0.15f;
    }

    /**
     * 对横向滑动手势的敏感程度。0为迟钝 1为敏感
     *
     * @return
     */
    protected float getSensitivity() {
        return 0.45f;
    }

    /**
     * 触发关闭Activity百分比
     *
     * @return
     */
    protected float getClosePercent() {
        return 0.4f;
    }

    /**
     * @return
     */
    public DownloadReceiver getDownloadReceiver() {
        if (appliction != null) {
            return appliction.getDownloadReceiver();
        } else if (IMovieAppliction.app != null) {
            IMovieAppliction.app.getDownloadReceiver();
        }
        return null;
    }

    public Picasso getPicasso() {
        if (appliction != null) {
            return appliction.getPicasso();
        } else if (IMovieAppliction.app != null) {
            IMovieAppliction.app.getPicasso();
        }
        return null;
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(AppEvent event) {
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroudEvent(AppEvent event) {
    }

    /**
     * @param event
     */
    public void postAppEvent(AppEvent event) {
        EventBus.getDefault().post(event);
    }

    /**
     * @param savedInstanceState
     */
    private void init(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getLayoutInflater();
        mMainView = getLayoutView(layoutInflater, getLayout());
        if (mMainView == null) return;
        setContentView(mMainView);
        ButterKnife.inject(this);
        //等所有的view初始化完成之后，
        //再来进行界面数据的初始化
        mMainView.post(new InflaterRunnable(this, mMainView, savedInstanceState, layoutInflater));
    }

    /**
     * @return
     */
    public LiteOrm getLiteOrm() {
        if (mLiteOrm == null)
            if (appliction != null) {
                mLiteOrm = LiteOrmManager.getInstance(appliction).getLiteOrm("imovie.db");
            } else if (IMovieAppliction.app != null) {
                mLiteOrm = LiteOrmManager.getInstance(IMovieAppliction.app).getLiteOrm("imovie.db");
            }
        return mLiteOrm;
    }

    /**
     * @return
     */
    public RetrofitManager getRetrofitManager() {
        if (mRetrofitManager == null)
            mRetrofitManager = RetrofitManager.with(this);
        return mRetrofitManager;
    }


    /**
     * 获取当前登录用户
     *
     * @return
     */
    public User getLoginUser() {
        return User.getLoginUser();
    }

    /**
     * @return
     */
    public boolean checkLogin() {
        if (getLoginUser() == null) {
            showToast(getString(R.string.please_login));
            startActivity(LoginActivity.class);
            return false;
        }
        return true;
    }

    /**
     * @param attr
     * @return
     */
    public int getAttributeValue(int attr) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }


    /**
     * 当前activity布局
     *
     * @return
     */
    protected abstract int getLayout();

    /**
     * @param inflater
     * @param layout
     * @return
     */
    protected View getLayoutView(LayoutInflater inflater, int layout) {
        if (layout <= 0) return null;
        return inflater.inflate(layout, null, false);
    }

    /**
     * 设置监听器
     */
    protected void setListener() {
    }

    /**
     * 查找必要控件
     *
     * @param view
     */
    protected void findView(View view) {
    }

    /**
     * 初始化页面数据
     *
     * @param savedState
     * @param inflater
     */
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
    }

    /**
     * 按两次退出当前activity
     * <p/>
     * 默认一次
     *
     * @return
     */
    protected boolean isDoubleFinish() {
        return false;
    }

    /**
     * 开启一个Activity
     *
     * @param clazz
     */
    public void startActivity(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
    }

    /**
     * 为关闭当前activity添加淡入淡出的动画
     */
    @Override
    public void finish() {
        long timeMillis = System.currentTimeMillis();
        if (isDoubleFinish() && timeMillis - preTime > 2000) {
            showToast(getString(R.string.finish_hit));
            preTime = timeMillis;
            return;
        }
        super.finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

    /**
     * 开启一个Activity
     *
     * @param clazz
     * @param bundle
     */
    public void startActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 开启一个Service
     *
     * @param clazz
     */
    public void startService(Class<?> clazz) {
        startService(new Intent(this, clazz));
    }

    /**
     * 开启一个Service
     *
     * @param clazz
     * @param bundle
     */
    public void startService(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startService(intent);
    }

    /**
     * @param clazz
     * @param code
     */
    public void startActivityForResult(Class<?> clazz, int code) {
        startActivityForResult(new Intent(this, clazz), code);
    }

    /**
     * @param clazz
     * @param bundle
     * @param code
     */
    public void startActivityForResult(Class<?> clazz, Bundle bundle, int code) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivityForResult(intent, code);
    }

    /**
     * @param editText
     * @return
     */
    public String getEditTextString(EditText editText) {
        if (editText == null) return "";
        return editText.getText().toString().trim();
    }

    public String getTextViewString(TextView editText) {
        if (editText == null) return "";
        return editText.getText().toString().trim();
    }

    /**
     * 获取当前用户可见Fragment
     *
     * @return 可能为空
     */
    public Fragment getVisibleFragment() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        for (Fragment f : fragments) {
            if (f.getUserVisibleHint())
                return f;
        }
        return null;
    }

    public Fragment getUnVisibleFragment() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        for (Fragment f : fragments) {
            if (!f.getUserVisibleHint())
                return f;
        }
        return null;
    }

    /**
     * @param id
     * @param name
     * @param f
     */
    public void changeFragment(int id, String name, Fragment f) {
        if (isFinishing()) return;
        FragmentManager fm = getSupportFragmentManager();
        if (fm == null) return;
        // 切换动画
        FragmentTransaction ft = fm.beginTransaction();
        // 替换布局为fragment
        ft.replace(id, f);
        // 将当前fragment添加到Application列表里面
        if (!TextUtils.isEmpty(name)) ft.addToBackStack(name);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commitAllowingStateLoss();
    }

    /**
     * @param id
     * @param f
     */
    public void changeFragment(int id, Fragment f) {
        changeFragment(id, null, f);
    }

    /**
     * @param id
     */
    public void showToast(int id) {
        String string = getResources().getString(id);
        showToast(string);
    }

    /**
     * @param c
     */
    public void showToast(CharSequence c) {
        showToast(c, Toast.LENGTH_SHORT);
    }

    /**
     * @param id
     * @param len
     */
    public void showToast(int id, int len) {
        String string = getResources().getString(id);
        showToast(string, len);
    }

    /**
     * @param c
     * @param len
     */
    public void showToast(final CharSequence c, final int len) {
        if ("main".equals(Thread.currentThread().getName())) {
            makeToast(c, len);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeToast(c, len);
                }
            });
        }
    }

    /**
     * @param c
     * @param len
     */
    private void makeToast(CharSequence c, int len) {
        Toast.makeText(this, c, len).show();
    }

    /**
     * @param view
     * @param c
     * @param len
     */
    public void showSnackbar(View view, CharSequence c, int len) {
        showSnackbar(view, c, len, null, null);
    }

    /**
     * @param view
     * @param c
     */
    public void showSnackbar(View view, CharSequence c) {
        showSnackbar(view, c, Snackbar.LENGTH_SHORT, null, null);
    }

    /**
     * @param c
     */
    public void showSnackbar(CharSequence c) {
        if (mMainView != null)
            showSnackbar(mMainView, c, Snackbar.LENGTH_SHORT, null, null);
    }

    /**
     * @param view
     * @param c
     * @param title
     * @param l
     */
    public void showSnackbar(View view, CharSequence c, CharSequence title, View.OnClickListener l) {
        showSnackbar(view, c, Snackbar.LENGTH_SHORT, title, l);
    }

    /**
     * @param view
     * @param c
     * @param len
     * @param title
     * @param l
     */
    public void showSnackbar(final View view, final CharSequence c, final int len, final CharSequence title, final View.OnClickListener l) {
        if ("main".equals(Thread.currentThread().getName())) {
            makeSnackbar(view, c, len, title, l);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeSnackbar(view, c, len, title, l);
                }
            });
        }
    }

    /**
     * @param view
     * @param c
     * @param len
     * @param title
     * @param l
     */
    private void makeSnackbar(View view, CharSequence c, int len, CharSequence title, View.OnClickListener l) {
        Snackbar.make(view, c, len).setAction(title, l).show();
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        try {
            if (!mBroadcastReceiver.contains(receiver)) {
                mBroadcastReceiver.add(receiver);
                return super.registerReceiver(receiver, filter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        try {
            if (!mBroadcastReceiver.contains(receiver)) {
                mBroadcastReceiver.add(receiver);
                return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        try {
            if (mBroadcastReceiver.contains(receiver)) {
                mBroadcastReceiver.remove(receiver);
                super.unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private static class InflaterRunnable implements Runnable {
        private View view;
        private Bundle savedState;
        private LayoutInflater inflater;
        private SoftReference<BaseActivity> softReference;

        public InflaterRunnable(BaseActivity activity, View view, Bundle savedState, LayoutInflater inflater) {
            softReference = new SoftReference<>(activity);
            this.view = view;
            this.savedState = savedState;
            this.inflater = inflater;
        }

        @Override
        public void run() {
            BaseActivity baseActivity = softReference.get();
            if (baseActivity == null || baseActivity.isDestroy) return;
            baseActivity.findView(view);
            baseActivity.initActivity(savedState, inflater);
            baseActivity.setListener();
        }
    }
}
