package com.fanchen.imovie.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.AppEvent;
import com.fanchen.imovie.entity.bmob.User;
import com.fanchen.imovie.fragment.HomePagerFragment;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.view.CircleImageView;
import com.squareup.picasso.Picasso;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        AppCompatDelegate.setDefaultNightMode(mSharedPreferences.getBoolean("swith_mode", false) ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
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
        if (mSharedPreferences.getBoolean("swith_mode", false)) {
            mSwitchMode.setImageResource(R.drawable.ic_switch_daily);
        } else {
            mSwitchMode.setImageResource(R.drawable.ic_switch_night);
        }
        //设置用户名 签名
        setLoginInfo(getLoginUser());
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("auto_updata", true)) {
            //自动检查更新
            BmobUpdateAgent.setUpdateOnlyWifi(false);
            BmobUpdateAgent.update(this);
        }
        disableNavigationViewScrollbars();
        FragmentManager sfm = getSupportFragmentManager();
        if (sfm.findFragmentByTag(MainActivity.class.getName()) == null) {
            if (preferences.getBoolean("more_hit", true)) {
                DialogUtil.showMaterialDialog(this, getString(R.string.more_hit), "继续提醒", "不要再说了", new OnButtonClickListener() {
                    @Override
                    public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
                        dialog.dismiss();
                        if (btn == OnButtonClickListener.RIGHT) {
                            preferences.edit().putBoolean("more_hit", false).commit();
                        }
                    }
                });
            }
            HomePagerFragment homePagerFragment = new HomePagerFragment();
            FragmentTransaction ft = sfm.beginTransaction();
            ft.add(R.id.fl_main_content, homePagerFragment, MainActivity.class.getName()).show(homePagerFragment).commitAllowingStateLoss();
        }
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
                new PicassoWrap(Picasso.with(appliction)).loadVertical(user.getHeaderUrl(), mUserAvatarView);
            } else if (user.getHeader() != null && appliction != null) {
                new PicassoWrap(Picasso.with(appliction)).loadVertical(user.getHeader().getFileUrl(appliction), mUserAvatarView);
            }
        } else {
            mUserName.setText(getString(R.string.not_login));
            mUserLevel.setVisibility(View.GONE);
            mUserBirthday.setVisibility(View.GONE);
            mUserAvatarView.setImageResource(R.drawable.ico_user_default);
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
        if (mSharedPreferences.getBoolean("swith_mode", false)) {// 日间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            mSharedPreferences.edit().putBoolean("swith_mode", false).commit();
        } else { // 夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            mSharedPreferences.edit().putBoolean("swith_mode", true).commit();
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
                case R.id.item_live:
                    TvLiveTabActivity.startActivity(this);
                    break;
                case R.id.item_girl:
                    GirlTabActivity.startActivity(this);
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
}
