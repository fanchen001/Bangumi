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
import com.fanchen.imovie.entity.AppEvent
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.KeyBoardUtils
import com.fanchen.imovie.util.SmsUtil
import kotlinx.android.synthetic.main.activity_inputcode.*

/**
 * 注册时输入验证吗页面
 */
class InputCodeActivity : BaseToolbarActivity(), View.OnClickListener, TextWatcher {
    override val activityTitle: String
        get() = getString(R.string.input_code)

    private val msmListener = object : SmsUtil.OnMsmListener {

        override fun onSuccess(event: Int) {
            // 提交验证码成功
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                // 验证成功回调
                SetPwdActivity.startActivity(this@InputCodeActivity, intent.getStringExtra(PHONE))
            } else {
                showSnackbar(getString(R.string.get_code_success))
            }
        }

        override fun onFinal(e: Throwable) {
            showSnackbar(e.toString())
        }

    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SmsUtil.register()
        registerReceiver(msgReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    override fun getLayout(): Int {
        return R.layout.activity_inputcode
    }

    override fun setListener() {
        super.setListener()
        et_verification.addTextChangedListener(this)
        btn_sendmessage.setOnClickListener(this)
        tv_cannotverification.setOnClickListener(this)
        btn_next.setOnClickListener(this)
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onMainEvent(event: AppEvent) {
        if (event.what == AppEvent.REGISTER_SUCCESS && SetPwdActivity::class.java.name == event.from) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(msgReceiver)
        SmsUtil.unRegister()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sendmessage ->
                //获取验证码
                SmsUtil.getVerificationCode(intent.getStringExtra(PHONE), msmListener)
            R.id.tv_cannotverification -> DialogUtil.showBottomCancleDialog(this)
            R.id.btn_next -> {
                KeyBoardUtils.closeKeyboard(this, et_verification)
                val verification = getEditTextString(et_verification)
                if (!TextUtils.isEmpty(verification)) {
                    SmsUtil.submitVerificationCode(intent.getStringExtra(PHONE), verification)
                } else {
                    showSnackbar(getString(R.string.non_verification))
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.length >= 4) {
            btn_next.isEnabled = true
            btn_next.isClickable = true
        } else {
            btn_next.isEnabled = false
            btn_next.isClickable = false
        }
    }

    override fun afterTextChanged(s: Editable) {}

    companion object {

        const val PHONE = "phone"

        fun startActivity(context: Context, phone: String) {
            try {
                val intent = Intent(context, InputCodeActivity::class.java)
                intent.putExtra(PHONE, phone)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
