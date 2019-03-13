package com.fanchen.imovie.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.entity.bmob.BmobObj
import com.fanchen.imovie.entity.bmob.Feedback
import com.fanchen.imovie.util.DialogUtil
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedBackActivity : BaseToolbarActivity(), View.OnClickListener {
    override val activityTitle: String
        get() = getString(R.string.feedback)

    private var feedbackType = Feedback.TYPE_OTHER

    private val saveListener = object : BmobObj.OnRefreshListener() {

        override fun onStart() {
            DialogUtil.showProgressDialog(this@FeedBackActivity, getString(R.string.loading))
        }

        override fun onFinish() {
            DialogUtil.closeProgressDialog()
        }

        override fun onSuccess() {
            et_contact.setText("")
            et_feedback.setText("")
            showSnackbar(getString(R.string.send_success))
        }

        override fun onFailure(i: Int, s: String) {
            showSnackbar(s)
        }

    }

    override fun getLayout(): Int {
        return R.layout.activity_feedback
    }

    override fun setListener() {
        super.setListener()
        btn_product_bug.setOnClickListener(this)
        btn_chapter_pictures.setOnClickListener(this)
        btn_comic_cache.setOnClickListener(this)
        btn_complete.setOnClickListener(this)
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        tv_product_bug.setTextColor(resources.getColor(R.color.text_black))
        tv_chapter_pictures.setTextColor(resources.getColor(R.color.text_black))
        tv_comic_cache.setTextColor(resources.getColor(R.color.comm_red_high))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_comic_cache -> {
                feedbackType = Feedback.TYPE_OTHER
                tv_product_bug.setTextColor(resources.getColor(R.color.text_black))
                tv_chapter_pictures.setTextColor(resources.getColor(R.color.text_black))
                tv_comic_cache.setTextColor(resources.getColor(R.color.comm_red_high))
            }
            R.id.btn_chapter_pictures -> {
                feedbackType = Feedback.TYPE_SUGGEST
                tv_product_bug.setTextColor(resources.getColor(R.color.text_black))
                tv_comic_cache.setTextColor(resources.getColor(R.color.text_black))
                tv_chapter_pictures.setTextColor(resources.getColor(R.color.comm_red_high))
            }
            R.id.btn_product_bug -> {
                feedbackType = Feedback.TYPE_BUG
                tv_comic_cache.setTextColor(resources.getColor(R.color.text_black))
                tv_chapter_pictures.setTextColor(resources.getColor(R.color.text_black))
                tv_product_bug.setTextColor(resources.getColor(R.color.comm_red_high))
            }
            R.id.btn_complete -> {
                if (!checkLogin()) {
                    return
                }
                val feedback = getEditTextString(et_feedback)
                val contact = getEditTextString(et_contact)
                if (TextUtils.isEmpty(feedback)) {
                    showSnackbar(getString(R.string.error_feedback_null))
                    return
                }
                val feed = Feedback()
                feed.email = contact
                feed.content = feedback
                feed.user = loginUser
                feed.type = feedbackType
                feed.save(saveListener)
            }
        }
    }
}
