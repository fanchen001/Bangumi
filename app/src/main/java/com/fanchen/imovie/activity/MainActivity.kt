package com.fanchen.imovie.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.support.design.internal.NavigationMenuView
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatDelegate
import android.text.Html
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.v3.update.BmobUpdateAgent
import com.fanchen.imovie.IMovieAppliction
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseActivity
import com.fanchen.imovie.dialog.MainBannerDialog
import com.fanchen.imovie.dialog.OnButtonClickListener
import com.fanchen.imovie.entity.AppEvent
import com.fanchen.imovie.entity.bmob.User
import com.fanchen.imovie.fragment.HomePagerFragment
import com.fanchen.imovie.picasso.PicassoWrap
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.SystemUtil
import com.fanchen.imovie.view.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * 主界面
 */
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DrawerLayout.DrawerListener {
    private var mUserName: TextView? = null
    private var mUserBirthday: TextView? = null
    private var mUserLevel: TextView? = null
    private var mSwitchMode: ImageView? = null
    private var mUserAvatarView: CircleImageView? = null

    private var item: MenuItem? = null
    private var v: View? = null
    private lateinit var mSharedPreferences: SharedPreferences
    private var lastTime = System.currentTimeMillis()
    

    private val buttonClickListener = OnButtonClickListener { dialog, btn ->
        dialog.dismiss()
        if (btn == OnButtonClickListener.RIGHT) {
            mSharedPreferences.edit().putBoolean("new_class_hit", false).apply()
        }
    }

    private val appClickListener = OnButtonClickListener { dialog, btn ->
        dialog.dismiss()
        if (btn == OnButtonClickListener.RIGHT) {
            mSharedPreferences.edit().putBoolean("app_hit", false).apply()
            SystemUtil.startThreeApp(this@MainActivity, "https://www.coolapk.com/apk/com.fanchen.aisou")
        }
    }

    private val tipRunnable = Runnable {
        if (isFinishing) return@Runnable
        DialogUtil.showCancelableDialog(this@MainActivity, Html.fromHtml(getString(R.string.more_hit)), "继续提醒", "不要再说了", buttonClickListener)
    }


    private val appRunnable = Runnable {
        if (isFinishing) return@Runnable
        DialogUtil.showCancelableDialog(this@MainActivity, getString(R.string.app_hit), "滚,就不下载", "好,马上下载", appClickListener)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        AppCompatDelegate.setDefaultNightMode(if (mSharedPreferences.getBoolean("swith_mode", true)) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun findView(view: View) {
        val headerView = navigation_view.getHeaderView(0)
        mUserAvatarView = headerView.findViewById<View>(R.id.user_avatar_view) as CircleImageView
        mUserName = headerView.findViewById<View>(R.id.user_name) as TextView
        mUserLevel = headerView.findViewById<View>(R.id.tv_main_level) as TextView
        mUserBirthday = headerView.findViewById<View>(R.id.tv_main_birthday) as TextView
        mSwitchMode = headerView.findViewById<View>(R.id.iv_head_switch_mode) as ImageView
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        disableNavigationViewScrollbars()
        //设置用户名 签名
        setLoginInfo(loginUser)
        if (mSharedPreferences.getBoolean("auto_updata", true)) {
            //自动检查更新
            BmobUpdateAgent.setUpdateOnlyWifi(false)
            BmobUpdateAgent.update(this)
        }
        val sfm = supportFragmentManager
        if (sfm.findFragmentByTag(MainActivity::class.java.name) == null) {
            val handler = Handler(Looper.getMainLooper())
            if (mSharedPreferences.getBoolean("new_class_hit", true)) {
                handler.postDelayed(tipRunnable, 2000)
            }
            if (mSharedPreferences.getBoolean("app_hit", true) && Random().nextInt(10) == 1) {
                handler.postDelayed(appRunnable, 5000)
            }
            val homePagerFragment = HomePagerFragment()
            val ft = sfm.beginTransaction()
            ft.add(R.id.fl_main_content, homePagerFragment, MainActivity::class.java.name).show(homePagerFragment).commitAllowingStateLoss()
        }
        MainBannerDialog.showMainBanner(this)
    }

    override fun setListener() {
        navigation_view.setNavigationItemSelectedListener(this)
        //设置日夜间模式切换
        mSwitchMode?.setOnClickListener(this)
        mUserAvatarView?.setOnClickListener(this)
        drawer_layout.addDrawerListener(this)
        navigation_view.setNavigationItemSelectedListener(this)
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun isSwipeActivity(): Boolean {
        return false
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event != null && event.keyCode == KeyEvent.KEYCODE_MENU) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                toggleDrawer()
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onBackPressed() {
        if (drawer_layout != null && drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            val time = System.currentTimeMillis()
            if (time - lastTime < 3000) {
                super.onBackPressed()
            } else {
                lastTime = time
                showSnackbar("在按一次退出程序")
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        this.item = item
        if (drawer_layout != null && drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * @param user
     */
    private fun setLoginInfo(user: User?) {
        mSwitchMode?.setImageResource(if (mSharedPreferences.getBoolean("swith_mode", true)) R.drawable.ic_switch_daily else R.drawable.ic_switch_night)
        if (user != null) {
            mUserBirthday?.text = user.birthday
            when {
                user.level == User.LEVEL_ADMIN -> mUserLevel?.text = getString(R.string.admin)
                user.level == User.LEVEL_SVIP -> mUserLevel?.text = getString(R.string.svip)
                user.level == User.LEVEL_VIP -> mUserLevel?.text = getString(R.string.vip)
                else -> mUserLevel?.text = getString(R.string.user_non)
            }
            mUserLevel?.visibility = View.VISIBLE
            mUserBirthday?.visibility = View.VISIBLE
            mUserName?.text = user.nickName
            if (!TextUtils.isEmpty(user.headerUrl) && appliction != null) {
                PicassoWrap(picasso).loadVertical(user.headerUrl, mUserAvatarView)
            } else if (user.header != null && appliction != null) {
                PicassoWrap(picasso).loadVertical(user.header.getFileUrl(appliction), mUserAvatarView)
            }
        } else {
            mUserName?.text = getString(R.string.not_login)
            mUserLevel?.visibility = View.GONE
            mUserBirthday?.visibility = View.GONE
            mUserAvatarView?.setImageResource(R.drawable.ico_user_default)
        }
    }

    private fun startAlipay() {
        try {
            val mIntent = Intent()
            mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mIntent.action = "android.intent.action.VIEW"
            mIntent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity.alias")
            mIntent.data = Uri.parse(IMovieAppliction.ALIPAYS)
            startActivity(mIntent)
        } catch (e: Exception) {
            try {
                showToast("请先下载支付宝")
                val url = "https://ds.alipay.com/?from=mobileweb"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (ee: Exception) {
                ee.printStackTrace()
            }
        }
    }

    /**
     * DrawerLayout侧滑菜单开关
     */
    fun toggleDrawer() {
        if (drawer_layout == null) return
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    private fun disableNavigationViewScrollbars() {
        if (navigation_view != null) {
            val navigationMenuView = navigation_view.getChildAt(0) as NavigationMenuView
            navigationMenuView.isVerticalScrollBarEnabled = false
        }
    }

    /**
     * 日夜间模式切换
     */
    private fun togoNightMode() {
        if (mSharedPreferences.getBoolean("swith_mode", true)) {// 日间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            mSharedPreferences.edit().putBoolean("swith_mode", false).apply()
        } else { // 夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            mSharedPreferences.edit().putBoolean("swith_mode", true).apply()
        }
        recreate()
    }

    override fun onClick(v: View) {
        this.v = v
        if (drawer_layout != null && drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

    override fun onDrawerOpened(drawerView: View) {}

    override fun onMainEvent(event: AppEvent) {
        //登录的事件
        if (LoginActivity::class.java.name == event.from && AppEvent.LOGIN == event.what) {
            setLoginInfo(event.data as User)
        } else if (UserActivity::class.java.name == event.from && AppEvent.LOGOUT == event.what) {
            setLoginInfo(null)
        } else if (AppEvent.UPDATE == event.what) {
            setLoginInfo(loginUser)
        }
    }

    override fun onDrawerClosed(drawerView: View) {
        if (item != null) {
            when (item!!.itemId) {
                R.id.item_app -> ApkListActivity.startActivity(this, ApkListActivity.TYPE_APK)
                R.id.item_download -> DownloadTabActivity.startActivity(this)
                R.id.item_favourite -> if (checkLogin()) {
                    CollectTabActivity.startActivity(this)
                }
                R.id.item_history -> HistoryActivity.startActivity(this)
                R.id.item_settings -> SettingsActivity.startActivity(this)
                R.id.item_girl -> GirlTabActivity.startActivity(this)
                R.id.item_tv -> {
                    val string = mSharedPreferences.getString("lives", "ican")
                    if ("hlzb".equals(string, ignoreCase = true)) {
                        TvLiveActivity.startActivity(this)
                    } else {
                        VideoTabActivity.startLiveActivity(this, string)
                    }
                }
                R.id.item_short -> ShortVideoTabActivity.startActivity(this)
            }
            item = null
        } else if (v != null) {
            when (v!!.id) {
                R.id.user_avatar_view -> if (loginUser != null) {
                    UserActivity.startActivity(this)
                } else {
                    LoginActivity.startActivity(this)
                }
                R.id.iv_head_switch_mode -> togoNightMode()
            }
            v = null
        }

    }

    override fun onDrawerStateChanged(newState: Int) {

    }

    companion object {

        fun startActivity(context: Context) {
            try {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
