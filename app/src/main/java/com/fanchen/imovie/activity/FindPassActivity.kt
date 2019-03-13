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
import cn.bmob.v3.BmobQuery
import cn.smssdk.SMSSDK
import com.fanchen.imovie.IMovieAppliction
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.entity.bmob.User
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.KeyBoardUtils
import com.fanchen.imovie.util.SmsUtil
import kotlinx.android.synthetic.main.activity_findpass.*

/**
 * 找回密码
 */
class FindPassActivity : BaseToolbarActivity(), TextWatcher, View.OnClickListener {
    override val activityTitle: String
        get() = getString(R.string.find_pass)
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
                val query = BmobQuery<User>()
                query.addWhereEqualTo("phone", phone)
                findListener.onStart()
                query.findObjects(IMovieAppliction.app, findListener)
            } else {
                showSnackbar(getString(R.string.get_code_success))
            }
        }

        override fun onFinal(e: Throwable) {
            showSnackbar(e.toString())
        }

    }

    private val findListener = object : User.OnFindListener<User>() {

        override fun onStart() {
            DialogUtil.showProgressDialog(this@FindPassActivity, getString(R.string.find_pass_ing))
        }

        override fun onFinish() {
            DialogUtil.closeProgressDialog()
        }

        override fun onSuccess(list: List<User>?) {
            if (list != null && list.isNotEmpty() && !isFinishing) {
                DialogUtil.showMessageDialog(this@FindPassActivity, getString(R.string.your_pwd) + list[0].password)
            } else {
                showSnackbar(getString(R.string.find_pass_error))
            }
        }

        override fun onError(i: Int, s: String) {
            showSnackbar(s)
        }

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

    override fun getLayout(): Int {
        return R.layout.activity_findpass
    }

    override fun setListener() {
        super.setListener()
        find_password_delete.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        tv_cannotverification.setOnClickListener(this)
        btn_sendmessage.setOnClickListener(this)
        et_phonenumber.addTextChangedListener(this)
        et_verification.addTextChangedListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.find_password_delete -> et_phonenumber.text = null
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
            R.id.btn_next -> {
                KeyBoardUtils.closeKeyboard(this, et_phonenumber)
                KeyBoardUtils.closeKeyboard(this, et_verification)
                val phoneNumber = getEditTextString(et_phonenumber)
                val verification = getEditTextString(et_verification)
                if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(verification)) {
                    showSnackbar(getString(R.string.error_null))
                    return
                }
                SmsUtil.submitVerificationCode(phoneNumber, verification)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(getEditTextString(et_phonenumber)) && !TextUtils.isEmpty(getEditTextString(et_verification))) {
            btn_next.isEnabled = true
            btn_next.isClickable = true
        } else {
            btn_next.isEnabled = false
            btn_next.isClickable = false
        }
        if (!TextUtils.isEmpty(getEditTextString(et_phonenumber))) {
            find_password_delete.visibility = View.VISIBLE
        } else {
            find_password_delete.visibility = View.GONE
        }
    }

    override fun afterTextChanged(s: Editable) {}

    companion object {

        fun startActivity(context: Context) {
            try {
                val intent = Intent(context, FindPassActivity::class.java)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
