package com.fanchen.imovie.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.entity.AppEvent
import com.fanchen.imovie.entity.bmob.BmobObj
import com.fanchen.imovie.entity.bmob.User
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.KeyBoardUtils
import kotlinx.android.synthetic.main.activity_setpwd.*

/**
 * 设置密码
 */
class SetPwdActivity : BaseToolbarActivity(), View.OnClickListener, TextWatcher {
    override val activityTitle: String
        get() = getString(R.string.set_pwd)

    private val registerListener = object : BmobObj.OnRefreshListener() {

        override fun onStart() {
            DialogUtil.showProgressDialog(this@SetPwdActivity, getString(R.string.register_ing))
        }

        override fun onFinish() {
            DialogUtil.closeProgressDialog()
        }

        override fun onSuccess() {
            showToast(getString(R.string.register_success))
            postAppEvent(AppEvent(SetPwdActivity::class.java, AppEvent.REGISTER_SUCCESS))
        }

        override fun onFailure(i: Int, s: String) {
            showSnackbar(s)
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_setpwd
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        tv_username.text = intent.getStringExtra(PHONE)
    }

    override fun setListener() {
        super.setListener()
        et_password.addTextChangedListener(this)
        btn_password_visible.setOnClickListener(this)
        btn_finish.setOnClickListener(this)
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onMainEvent(event: AppEvent) {
        if (event.what == AppEvent.REGISTER_SUCCESS && SetPwdActivity::class.java.name == event.from) {
            finish()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_finish -> {
                KeyBoardUtils.closeKeyboard(this, et_password)
                val pwd = getEditTextString(et_password)
                if (!TextUtils.isEmpty(pwd) && pwd.length >= 6) {
                    val phone = intent.getStringExtra(PHONE)
                    val user = User(phone, pwd)
                    user.phone = phone
                    user.phoneVerified = true
                    user.register(registerListener)
                } else {
                    showSnackbar(getString(R.string.error_phone_pwd))
                }
            }
            R.id.btn_password_visible -> {
                val inputType = et_password.inputType
                if (inputType == InputType.TYPE_CLASS_TEXT) {
                    btn_password_visible.setImageResource(R.drawable.login_pss_invisable)
                    et_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                } else {
                    btn_password_visible.setImageResource(R.drawable.login_pass_visable)
                    et_password.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.length >= 6) {
            btn_finish.isEnabled = true
            btn_finish.isClickable = true
        } else {
            btn_finish.isEnabled = false
            btn_finish.isClickable = false
        }
    }

    override fun afterTextChanged(s: Editable) {}

    companion object {
        const val PHONE = "phone"

        fun startActivity(context: Context, phone: String) {
            try {
                val intent = Intent(context, SetPwdActivity::class.java)
                intent.putExtra(PHONE, phone)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
