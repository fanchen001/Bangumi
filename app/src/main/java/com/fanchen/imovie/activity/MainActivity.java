package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.MainBannerDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.entity.bmob.User;
import com.fanchen.imovie.fragment.HomePagerFragment;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.SystemUtil;
import com.fanchen.imovie.view.CircleImageView;

import java.util.Random;

import butterknife.InjectView;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * 主界面
 *
 * @author fanchen
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, DrawerLayout.DrawerListener {

    @InjectView(R.id.navigation_view)
    protected NavigationView mNavigationView;
    @InjectView(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;

    private TextView mUserName;
    private TextView mUserBirthday;
    private TextView mUserLevel;
    private ImageView mSwitchMode;
    private CircleImageView mUserAvatarView;

    private MenuItem item;
    private View v;
    private SharedPreferences mSharedPreferences;
    private long lastTime = System.currentTimeMillis();

    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        AppCompatDelegate.setDefaultNightMode(mSharedPreferences.getBoolean("swith_mode", true) ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void findView(View view) {
        View headerView = mNavigationView.getHeaderView(0);
        mUserAvatarView = (CircleImageView) headerView.findViewById(R.id.user_avatar_view);
        mUserName = (TextView) headerView.findViewById(R.id.user_name);
        mUserLevel = (TextView) headerView.findViewById(R.id.tv_main_level);
        mUserBirthday = (TextView) headerView.findViewById(R.id.tv_main_birthday);
        mSwitchMode = (ImageView) headerView.findViewById(R.id.iv_head_switch_mode);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);


        disableNavigationViewScrollbars();
        //设置用户名 签名
        setLoginInfo(getLoginUser());
        if (mSharedPreferences.getBoolean("auto_updata", true)) {
            //自动检查更新
            BmobUpdateAgent.setUpdateOnlyWifi(false);
            BmobUpdateAgent.update(this);
        }

        FragmentManager sfm = getSupportFragmentManager();
        if (sfm.findFragmentByTag(MainActivity.class.getName()) == null) {
            Handler handler = new Handler(Looper.getMainLooper());
//            if (!mSharedPreferences.getString("alipay_time", "").equals(DateUtil.getCurrentDate("yyyy-MM-dd"))) {
//                handler.postDelayed(alipayRunnable, 200);
//            }
            if (mSharedPreferences.getBoolean("new_class_hit", true)) {
                handler.postDelayed(tipRunnable, 2000);
            }
            if (mSharedPreferences.getBoolean("app_hit", true) && new Random().nextInt(10) == 1) {
                handler.postDelayed(appRunnable, 5000);
            }
            HomePagerFragment homePagerFragment = new HomePagerFragment();
            FragmentTransaction ft = sfm.beginTransaction();
            ft.add(R.id.fl_main_content, homePagerFragment, MainActivity.class.getName()).show(homePagerFragment).commitAllowingStateLoss();
        }
        MainBannerDialog.showMainBanner(this);
    }

    @Override
    protected void setListener() {
        mNavigationView.setNavigationItemSelectedListener(this);
        //设置日夜间模式切换
        mSwitchMode.setOnClickListener(this);
        mUserAvatarView.setOnClickListener(this);
        mDrawerLayout.addDrawerListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected boolean isSwipeActivity() {
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                toggleDrawer();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            long time = System.currentTimeMillis();
            if (time - lastTime < 3000) {
                super.onBackPressed();
            } else {
                lastTime = time;
                showSnackbar("在按一次退出程序");
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        this.item = item;
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * @param user
     */
    private void setLoginInfo(User user) {
        mSwitchMode.setImageResource(mSharedPreferences.getBoolean("swith_mode", true) ? R.drawable.ic_switch_daily : R.drawable.ic_switch_night);
        if (user != null) {
            mUserBirthday.setText(user.getBirthday());
            if (user.getLevel() == User.LEVEL_ADMIN) {
                mUserLevel.setText(getString(R.string.admin));
            } else if (user.getLevel() == User.LEVEL_SVIP) {
                mUserLevel.setText(getString(R.string.svip));
            } else if (user.getLevel() == User.LEVEL_VIP) {
                mUserLevel.setText(getString(R.string.vip));
            } else {
                mUserLevel.setText(getString(R.string.user_non));
            }
            mUserLevel.setVisibility(View.VISIBLE);
            mUserBirthday.setVisibility(View.VISIBLE);
            mUserName.setText(user.getNickName());
            if (!TextUtils.isEmpty(user.getHeaderUrl()) && appliction != null) {
                new PicassoWrap(getPicasso()).loadVertical(user.getHeaderUrl(), mUserAvatarView);
            } else if (user.getHeader() != null && appliction != null) {
                new PicassoWrap(getPicasso()).loadVertical(user.getHeader().getFileUrl(appliction), mUserAvatarView);
            }
        } else {
            mUserName.setText(getString(R.string.not_login));
            mUserLevel.setVisibility(View.GONE);
            mUserBirthday.setVisibility(View.GONE);
            mUserAvatarView.setImageResource(R.drawable.ico_user_default);
        }
    }

    private void startAlipay() {
        try {
            Intent mIntent = new Intent();
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mIntent.setAction("android.intent.action.VIEW");
            mIntent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity.alias");
            //https://render.alipay.com/p/f/fd-j6lzqrgm/guiderofmklvtvw.html?channel=qrCode&shareId=2088702958620520&sign=2UqdV74VmNSDBAaBkyvz%2BvQ2R3N4mQrJ%2Bpg79QKkcMA%3D&scene=offlinePaymentNewSns&campStr=p1j%2BdzkZl018zOczaHT4Z5CLdPVCgrEXq89JsWOx1gdt05SIDMPg3PTxZbdPw9dL&token=c1x094332eotzkcdjjmx7bf
            mIntent.setData(Uri.parse(IMovieAppliction.ALIPAYS));
            startActivity(mIntent);
        } catch (Exception e) {
            try {
                showToast("请先下载支付宝");
                String url = "https://ds.alipay.com/?from=mobileweb";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    /**
     * DrawerLayout侧滑菜单开关
     */
    public void toggleDrawer() {
        if (mDrawerLayout == null) return;
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void disableNavigationViewScrollbars() {
        if (mNavigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) mNavigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    /**
     * 日夜间模式切换
     */
    private void togoNightMode() {
        if (mSharedPreferences.getBoolean("swith_mode", true)) {// 日间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            mSharedPreferences.edit().putBoolean("swith_mode", false).apply();
        } else { // 夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            mSharedPreferences.edit().putBoolean("swith_mode", true).apply();
        }
        recreate();
    }

    @Override
    public void onClick(View v) {
        this.v = v;
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onMainEvent(AppEvent event) {
        //登录的事件
        if (LoginActivity.class.getName().equals(event.from) && AppEvent.LOGIN == event.what) {
            setLoginInfo((User) event.data);
        } else if (UserActivity.class.getName().equals(event.from) && AppEvent.LOGOUT == event.what) {
            setLoginInfo(null);
        } else if (AppEvent.UPDATE == event.what) {
            setLoginInfo(getLoginUser());
        }
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (item != null) {
            switch (item.getItemId()) {
                case R.id.item_app:
                    ApkListActivity.startActivity(this, ApkListActivity.TYPE_APK);
                    break;
                case R.id.item_download:
                    DownloadTabActivity.startActivity(this);
                    break;
                case R.id.item_favourite:
                    if (checkLogin()) {
                        CollectTabActivity.startActivity(this);
                    }
                    break;
                case R.id.item_history:
                    HistoryActivity.startActivity(this);
                    break;
                case R.id.item_settings:
                    SettingsActivity.startActivity(this);
                    break;
                case R.id.item_girl:
                    GirlTabActivity.startActivity(this);
                    break;
                case R.id.item_tv:
                    if (mSharedPreferences == null) return;
                    String string = mSharedPreferences.getString("lives", "ican");
                    if ("hlzb".equalsIgnoreCase(string)) {
                        TvLiveActivity.startActivity(this);
                    } else {
                        VideoTabActivity.startLiveActivity(this, string);
                    }
                    break;
                case R.id.item_short:
                    ShortVideoTabActivity.startActivity(this);
                    break;
            }
            item = null;
        } else if (v != null) {
            switch (v.getId()) {
                case R.id.user_avatar_view:
                    if (getLoginUser() != null) {
                        UserActivity.startActivity(this);
                    } else {
                        LoginActivity.startActivity(this);
                    }
                    break;
                case R.id.iv_head_switch_mode:
                    togoNightMode();
                    break;
            }
            v = null;
        }

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

//    private OnButtonClickListener alipayClickListener = new OnButtonClickListener() {
//
//        @Override
//        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
//            dialog.dismiss();
//            if (btn == OnButtonClickListener.RIGHT) {
//                String currentDate = DateUtil.getCurrentDate("yyyy-MM-dd");
//                mSharedPreferences.edit().putString("alipay_time", currentDate).apply();
//                startAlipay();
//            } else {
//                showToast("你不是真爱(｀⌒´メ)");
//            }
//        }
//
//    };

    private OnButtonClickListener buttonClickListener = new OnButtonClickListener() {

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (btn == OnButtonClickListener.RIGHT) {
                mSharedPreferences.edit().putBoolean("new_class_hit", false).apply();
            }
        }

    };

    private OnButtonClickListener appClickListener = new OnButtonClickListener() {

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (btn == OnButtonClickListener.RIGHT) {
                mSharedPreferences.edit().putBoolean("app_hit", false).apply();
                SystemUtil.startThreeApp(MainActivity.this, "https://www.coolapk.com/apk/com.fanchen.aisou");
            }
        }

    };


//    private Runnable alipayRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            if (isFinishing()) return;
//            Spanned spanned = Html.fromHtml(getString(R.string.alipay_hit));
//            DialogUtil.showCancelableDialog(MainActivity.this,spanned, "滚,关我屁事", "好的,马上", alipayClickListener);
//        }
//
//    };

    private Runnable tipRunnable = new Runnable() {

        @Override
        public void run() {
            if (isFinishing()) return;
            DialogUtil.showCancelableDialog(MainActivity.this, Html.fromHtml(getString(R.string.more_hit)), "继续提醒", "不要再说了", buttonClickListener);
        }

    };


    private Runnable appRunnable = new Runnable() {

        @Override
        public void run() {
            if (isFinishing()) return;
            DialogUtil.showCancelableDialog(MainActivity.this, getString(R.string.app_hit), "滚,就不下载", "好,马上下载", appClickListener);
        }

    };

}
