package com.fanchen.imovie.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.entity.AppEvent
import com.fanchen.imovie.entity.bmob.BmobObj
import com.fanchen.imovie.entity.bmob.User
import com.fanchen.imovie.util.DateUtil
import com.fanchen.imovie.util.DialogUtil
import kotlinx.android.synthetic.main.activity_info.*
import java.util.*

class UserInfoActivity : BaseToolbarActivity(), View.OnClickListener {
    override val activityTitle: String
        get() = if (loginUser == null) "" else loginUser.nickName
    /**
     * 日期选择器对话框监听
     */
    private val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        val days: String = if (monthOfYear + 1 < 10) {
            if (dayOfMonth < 10) {
                StringBuffer().append(year).append("年").append("0").append(monthOfYear + 1).append("月").append("0").append(dayOfMonth).append("日").toString()
            } else {
                StringBuffer().append(year).append("年").append("0").append(monthOfYear + 1).append("月").append(dayOfMonth).append("日").toString()
            }
        } else {
            if (dayOfMonth < 10) {
                StringBuffer().append(year).append("年").append(monthOfYear + 1).append("月").append("0").append(dayOfMonth).append("日").toString()
            } else {
                StringBuffer().append(year).append("年").append(monthOfYear + 1).append("月").append(dayOfMonth).append("日").toString()
            }
        }
        user_profile_birth_textview.text = days
    }

    private val updateListener = object : BmobObj.OnUpdateListener() {

        override fun onStart() {
            DialogUtil.showProgressDialog(this@UserInfoActivity, getString(R.string.loading))
        }

        override fun onFinish() {
            DialogUtil.closeProgressDialog()
        }

        override fun onSuccess() {
            mTitleView.text = loginUser.nickName
            postAppEvent(AppEvent(AppEvent.UPDATE))
            showSnackbar(getString(R.string.success))
        }

        override fun onFailure(i: Int, s: String) {
            showSnackbar(s)
        }

    }

    override fun getLayout(): Int {
        return R.layout.activity_info
    }

    override fun setListener() {
        super.setListener()
        user_profile_birth_textview.setOnClickListener(this)
        user_profile_btn_save.setOnClickListener(this)
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        val loginUser = loginUser
        if (loginUser == null) {
            finish()
            return
        }
        if (loginUser.sex == User.SEX_WOMAN) {
            user_profile_sex_rg.check(R.id.user_profile_sex_female)
        } else {
            user_profile_sex_rg.check(R.id.user_profile_sex_male)
        }
        user_profile_nickname_et.setText(loginUser.nickName)
        user_profile_email_et.setText(loginUser.email)
        user_profile_birth_textview.text = loginUser.birthday
        if (!TextUtils.isEmpty(loginUser.headerUrl)) {
            picasso.load(loginUser.headerUrl).into(user_profile_iconset_iv)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.user_profile_birth_textview -> {
                val ca = Calendar.getInstance()
                ca.time = DateUtil.getDateByFormat(loginUser.birthday, "yyyy-MM-dd")
                DatePickerDialog(this, onDateSetListener, ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.user_profile_btn_save -> {
                val nickName = getEditTextString(user_profile_nickname_et)
                val email = getEditTextString(user_profile_email_et)
                val birthday = getTextViewString(user_profile_birth_textview)
                if (nickName.length < 4) {
                    showSnackbar(getString(R.string.name_4))
                    return
                } else if (TextUtils.isEmpty(birthday)) {
                    showSnackbar(getString(R.string.brithday_null))
                    return
                }
                val loginUser = loginUser
                loginUser.nickName = nickName
                loginUser.birthday = birthday
                loginUser.email = email
                loginUser.sex = if (user_profile_sex_rg.checkedRadioButtonId == R.id.user_profile_sex_male) User.SEX_MAN else User.SEX_WOMAN
                loginUser.update(updateListener)
            }
        }
    }

    companion object {
        fun startActivity(context: Context) {
            try {
                val intent = Intent(context, UserInfoActivity::class.java)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
