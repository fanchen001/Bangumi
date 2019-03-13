package com.fanchen.imovie.base

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import com.fanchen.imovie.R
import kotlinx.android.synthetic.main.activity_tab_pager.*

abstract class BaseTabActivity : BaseToolbarActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_tab_pager
    }

    var mViewPager:ViewPager? = null

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        val adapter = getAdapter(supportFragmentManager)
        if (!checkTabViewNull()) {
            view_pager.adapter = adapter
            view_pager.offscreenPageLimit = adapter.count
            sliding_tabs.setupWithViewPager(view_pager)
            sliding_tabs.tabMode = getTabMode(adapter)
            mViewPager = view_pager
        }
    }

    protected open fun getTabMode(adapter: PagerAdapter): Int {
        return if (adapter.count < 5) TabLayout.MODE_FIXED else TabLayout.MODE_SCROLLABLE
    }

    /**
     * @return
     */
    protected abstract fun getAdapter(fm: FragmentManager): PagerAdapter

    private fun checkTabViewNull(): Boolean {
        return sliding_tabs == null || view_pager == null
    }
}
