package com.fanchen.imovie.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.entity.AppEvent
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.KeyBoardUtils
import kotlinx.android.synthetic.main.activity_register.*

/**
 * 用户注册
 */
class RegisterActivity : BaseToolbarActivity(), View.OnClickListener, TextWatcher {
    override val activityTitle: String
        get() = getString(R.string.register_user)

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onMainEvent(event: AppEvent) {
        if (event.what == AppEvent.REGISTER_SUCCESS && SetPwdActivity::class.java.name == event.from) {
            finish()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_register
    }

    override fun setListener() {
        super.setListener()
        btn_next.setOnClickListener(this)
        img_delete.setOnClickListener(this)
        tv_forgetpassword.setOnClickListener(this)
        et_phonenumber.addTextChangedListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_delete -> et_phonenumber.text = null
            R.id.btn_next -> {
                val phone = getEditTextString(et_phonenumber)
                KeyBoardUtils.closeKeyboard(this, et_phonenumber)
                if (!TextUtils.isEmpty(phone) && phone.length == 11 && phone.startsWith("1")) {
                    InputCodeActivity.startActivity(this, phone)
                } else {
                    showSnackbar(getString(R.string.error_phone))
                }
            }
            R.id.tv_forgetpassword -> DialogUtil.showBottomAgreementDialog(this)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s == null) return
        btn_next.isEnabled = false
        btn_next.isClickable = false
        img_delete.visibility = View.GONE
        if (s.length > 1) {
            if (s.length == 11) {
                btn_next.isEnabled = true
                btn_next.isClickable = true
            } else {
                btn_next.isClickable = false
                btn_next.isEnabled = false
            }
            img_delete.visibility = View.VISIBLE
        }
    }

    override fun afterTextChanged(s: Editable) {}

    companion object {
        fun startActivity(context: Context) {
            try {
                val intent = Intent(context, RegisterActivity::class.java)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
