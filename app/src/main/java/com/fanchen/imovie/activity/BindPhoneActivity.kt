package com.fanchen.imovie.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import cn.smssdk.SMSSDK
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.entity.bmob.BmobObj
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.KeyBoardUtils
import com.fanchen.imovie.util.SmsUtil
import kotlinx.android.synthetic.main.activity_bindphone.*

class BindPhoneActivity : BaseToolbarActivity(), View.OnClickListener, TextWatcher {
    override val activityTitle: String
        get() = getString(R.string.bindphone)
    /**
     * 短信广播,自动填充验证码
     */
    private val msgReceiver = object : BroadcastReceiver() {

        val SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED"

        override fun onReceive(context: Context, intent: Intent?) {
            if (intent == null || intent.extras == null) return
            // 短信广播
            if (intent.action == SMS_ACTION) {
                // 获取拦截到的短信数据
                val extractCode = SmsUtil.extractCode(intent)
                et_verification.setText(extractCode)
            }
        }

    }

    private val msmListener = object : SmsUtil.OnMsmListener {

        override fun onSuccess(event: Int) {
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                // 验证成功回调
                val phone = getEditTextString(et_phonenumber)
                loginUser.phone = phone
                loginUser.update(updateListener)
            } else {
                showSnackbar(getString(R.string.get_code_success))
            }
        }

        override fun onFinal(e: Throwable) {
            showSnackbar(e.toString())
        }

    }

    private val updateListener = object : BmobObj.OnUpdateListener() {

        override fun onStart() {
            DialogUtil.showProgressDialog(this@BindPhoneActivity, getString(R.string.loading))
        }

        override fun onFinish() {
            DialogUtil.closeProgressDialog()
        }

        override fun onSuccess() {
            et_phonenumber.setText("")
            et_verification.setText("")
            showSnackbar(getString(R.string.bind_success))
        }

        override fun onFailure(i: Int, s: String) {
            showSnackbar(s)
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_bindphone
    }

    override fun setListener() {
        super.setListener()
        find_password_delete.setOnClickListener(this)
        btn_bind.setOnClickListener(this)
        tv_cannotverification.setOnClickListener(this)
        btn_sendmessage.setOnClickListener(this)
        et_phonenumber.addTextChangedListener(this)
        et_verification.addTextChangedListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SmsUtil.register()
        registerReceiver(msgReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    override fun onDestroy() {
        super.onDestroy()
        SmsUtil.unRegister()
        unregisterReceiver(msgReceiver)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.find_password_delete -> et_phonenumber.setText(null)
            R.id.btn_sendmessage -> {
                KeyBoardUtils.closeKeyboard(this, et_phonenumber)
                KeyBoardUtils.closeKeyboard(this, et_verification)
                val phone = getEditTextString(et_phonenumber)
                if (!TextUtils.isEmpty(phone) && phone.length == 11 && phone.startsWith("1")) {
                    //获取验证码
                    SmsUtil.getVerificationCode(phone, msmListener)
                } else {
                    btn_sendmessage.setStopDown(true)
                    showSnackbar(getString(R.string.error_phone))
                }
            }
            R.id.tv_cannotverification -> DialogUtil.showBottomCancleDialog(this)
            R.id.btn_bind -> {
                KeyBoardUtils.closeKeyboard(this, et_phonenumber)
                KeyBoardUtils.closeKeyboard(this, et_verification)
                val phoneNamber = getEditTextString(et_phonenumber)
                val verification = getEditTextString(et_verification)
                if (TextUtils.isEmpty(phoneNamber) || TextUtils.isEmpty(verification)) {
                    showSnackbar(getString(R.string.error_null))
                    return
                }
                SmsUtil.submitVerificationCode(phoneNamber, verification)
            }
        }
    }

    override fun afterTextChanged(s: Editable) {}

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(getEditTextString(et_phonenumber)) && !TextUtils.isEmpty(getEditTextString(et_verification))) {
            btn_bind.isEnabled = true
            btn_bind.isClickable = true
        } else {
            btn_bind.isEnabled = false
            btn_bind.isClickable = false
        }
        if (!TextUtils.isEmpty(getEditTextString(et_phonenumber))) {
            find_password_delete.visibility = View.VISIBLE
        } else {
            find_password_delete.visibility = View.GONE
        }
    }

    companion object {

        fun startActivity(context: Context) {
            try {
                val intent = Intent(context, BindPhoneActivity::class.java)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
