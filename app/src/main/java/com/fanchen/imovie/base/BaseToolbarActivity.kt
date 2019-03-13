package com.fanchen.imovie.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_toolbar.*

/**
 * 布局包含有<include layout="@layout/layout_toolbar"></include>
 * 的Activity
 */
abstract class BaseToolbarActivity : BaseActivity() {
    protected abstract val activityTitle: String

    lateinit var mBackView: ImageView
    lateinit var mTitleView: TextView
    lateinit var mToolbar: android.support.v7.widget.Toolbar

    private val finishClickListener = View.OnClickListener {
        if (!isFinishing)
            finish()
    }

    override fun setListener() {
        super.setListener()
        if (checkToolbarViewNull()) return
        iv_top_back.setOnClickListener(finishClickListener)
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        if (checkToolbarViewNull()) return
        setSupportActionBar(toolbar_top)
        tv_top_title!!.text = activityTitle
        mBackView = iv_top_back
        mTitleView = tv_top_title
        mToolbar = toolbar_top
    }

    private fun checkToolbarViewNull(): Boolean {
        return iv_top_back == null || tv_top_title == null || toolbar_top == null
    }
}
