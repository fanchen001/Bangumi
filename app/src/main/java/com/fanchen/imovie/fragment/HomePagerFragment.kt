package com.fanchen.imovie.fragment

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.fanchen.imovie.R
import com.fanchen.imovie.activity.*
import com.fanchen.imovie.adapter.pager.HomePagerAdapter
import com.fanchen.imovie.base.BaseFragment
import com.fanchen.imovie.dialog.fragment.SearchDialogFragment
import com.fanchen.imovie.entity.AppEvent
import com.fanchen.imovie.entity.bmob.User
import com.fanchen.imovie.entity.face.ISearchWord
import com.fanchen.imovie.picasso.PicassoWrap
import kotlinx.android.synthetic.main.fragment_home_pager.*

/**
 * HomePagerFragment
 */
class HomePagerFragment : BaseFragment(), Toolbar.OnMenuItemClickListener, SearchDialogFragment.OnSearchClickListener, View.OnClickListener {

    private var mHomePagerAdapter: HomePagerAdapter? = null
    private val mSearchFragment = SearchDialogFragment.newInstance()

    override fun getLayout(): Int {
        return R.layout.fragment_home_pager
    }

    override fun initFragment(savedInstanceState: Bundle?, args: Bundle?) {
        super.initFragment(savedInstanceState, args)
        setHasOptionsMenu(true)
        toolbar!!.title = ""
        setLoginInfo(if (activity == null) null else activity.loginUser)
        activity.setSupportActionBar(toolbar)
        mHomePagerAdapter = HomePagerAdapter(childFragmentManager)
        view_pager!!.offscreenPageLimit = 5
        view_pager!!.adapter = mHomePagerAdapter
        sliding_tabs!!.setupWithViewPager(view_pager)
        if (savedInstanceState != null) {
            view_pager!!.currentItem = savedInstanceState.getInt(CURRENT_ITEM)
        } else {
            //默认选中影视页
            view_pager!!.currentItem = 2
        }
    }

    override fun setListener() {
        super.setListener()
        toolbar!!.setOnMenuItemClickListener(this)
        toolbar!!.setOnClickListener(this)
        mSearchFragment.setOnSearchClickListener(this)
    }

    /**
     *
     * @param user
     */
    private fun setLoginInfo(user: User?) {
        if (user != null) {
            tv_home_name!!.text = user.nickName
            if (!TextUtils.isEmpty(user.headerUrl) && activity != null && activity.appliction != null) {
                PicassoWrap(picasso).loadVertical(user.headerUrl, toolbar_user_avatar)
            } else if (user.header != null && activity != null && activity.appliction != null) {
                PicassoWrap(picasso).loadVertical(user.header.getFileUrl(activity.appliction), toolbar_user_avatar)
            }
        } else {
            tv_home_name!!.text = getStringFix(R.string.not_login)
            toolbar_user_avatar!!.setImageResource(R.drawable.ico_user_default)
        }
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onMainEvent(event: AppEvent) {
        if (LoginActivity::class.java.name == event.from && AppEvent.LOGIN == event.what) {
            setLoginInfo(event.data as User)
        } else if (UserActivity::class.java.name == event.from && AppEvent.LOGOUT == event.what) {
            setLoginInfo(null)
        } else if (AppEvent.UPDATE == event.what && activity != null) {
            setLoginInfo(activity.loginUser)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (view_pager != null) {
            outState.putInt(CURRENT_ITEM, view_pager!!.currentItem)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onSearchClick(keyword: ISearchWord) {
        SearchBangumiActivity.startActivity(activity, keyword.word)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.clear()
        inflater!!.inflate(R.menu.menu_main, menu)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_action_search//点击搜索
            -> openSearchDialog()
            R.id.id_action_download -> DownloadTabActivity.startActivity(activity)
            R.id.id_action_game -> ApkListActivity.startActivity(activity, ApkListActivity.TYPE_GAME)
        }
        return true
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.toolbar -> if (activity != null && activity is MainActivity) {
                (activity as MainActivity).toggleDrawer()
            }
        }
    }

    fun openSearchDialog() {
        mSearchFragment?.show(getActivity()!!.supportFragmentManager, javaClass.simpleName)
    }

    companion object {
        val CURRENT_ITEM = "item"
    }

}
