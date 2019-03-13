package com.fanchen.imovie.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.listener.FindListener
import com.fanchen.imovie.IMovieAppliction
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.entity.AppEvent
import com.fanchen.imovie.entity.bmob.User
import com.fanchen.imovie.entity.bmob.UserAuth
import com.fanchen.imovie.entity.bmob.VideoCollect
import com.fanchen.imovie.thread.AsyTaskQueue
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.KeyBoardUtils
import com.fanchen.imovie.util.LogUtil
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 用户登录
 */
class LoginActivity : BaseToolbarActivity(), View.OnClickListener, View.OnFocusChangeListener, TextWatcher {
    override val activityTitle: String
        get() = getString(R.string.login_user)

    private val authListener = object : UMAuthListener {

        override fun onStart(share_media: SHARE_MEDIA) {
            showSnackbar(getString(R.string.login_start))
        }

        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
            var auth: UserAuth? = null
            when (share_media) {
                SHARE_MEDIA.QQ -> auth = UserAuth(UserAuth.SNS_TYPE_QQ, map["accessToken"], map["expires_in"], map["openid"])
                SHARE_MEDIA.WEIXIN -> auth = UserAuth(UserAuth.SNS_TYPE_WEIXIN, map["accessToken"], map["expires_in"], map["openid"])
                SHARE_MEDIA.SINA -> auth = UserAuth(UserAuth.SNS_TYPE_WEIBO, map["accessToken"], map["expires_in"], map["uid"])
            }
            auth?.login(loginListener, map)
        }

        override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
            showSnackbar(getString(R.string.login_error))
        }

        override fun onCancel(share_media: SHARE_MEDIA, i: Int) {
            showSnackbar(getString(R.string.login_cancel))
        }

    }

    private val loginListener = object : User.OnLoginListener() {

        override fun onStart() {
            DialogUtil.showProgressDialog(this@LoginActivity, getString(R.string.login_ing))
        }

        override fun onFinish() {
            DialogUtil.closeProgressDialog()
        }

        override fun onLoginSuccess(user: User) {
            synchronizationVideo(user)
            //发布登录事件
            postAppEvent(AppEvent(LoginActivity::class.java, AppEvent.LOGIN, user))
            showToast(getString(R.string.login_success))
            finish()
        }

        override fun onError(i: Int, s: String) {
            showSnackbar(s)
        }

    }

    private val findVideoListener = object : FindListener<VideoCollect>() {

        override fun onSuccess(list: List<VideoCollect>) {
            LogUtil.e(LoginActivity::class.java, "同步video成功")
            AsyTaskQueue.newInstance().execute(SaveTaskListener(list))
        }

        override fun onError(i: Int, s: String) {
            LogUtil.e(LoginActivity::class.java, "同步video失败")
        }

    }

    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    override fun setListener() {
        super.setListener()
        btn_password_visible.setOnClickListener(this)
        tv_forgetpassword.setOnClickListener(this)
        tv_registeraccount.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        ll_wechat_logo.setOnClickListener(this)
        ll_tencent_logo.setOnClickListener(this)
        ll_sina_logo.setOnClickListener(this)
        et_login_username.onFocusChangeListener = this
        et_login_username.addTextChangedListener(this)
        et_login_password.onFocusChangeListener = this
        et_login_password.addTextChangedListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_registeraccount -> RegisterActivity.startActivity(this)
            R.id.btn_password_visible -> {
                val inputType = et_login_password.inputType
                if (inputType == InputType.TYPE_CLASS_TEXT) {
                    btn_password_visible.setImageResource(R.drawable.login_pss_invisable)
                    et_login_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                } else {
                    btn_password_visible.setImageResource(R.drawable.login_pass_visable)
                    et_login_password.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
            R.id.tv_forgetpassword -> FindPassActivity.startActivity(this)
            R.id.btn_login -> {
                KeyBoardUtils.closeKeyboard(this, et_login_username)
                KeyBoardUtils.closeKeyboard(this, et_login_password)
                val user = getEditTextString(et_login_username)
                val pass = getEditTextString(et_login_password)
                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                    showSnackbar(getString(R.string.login_hit))
                    return
                }
                if (user.length < 4 || pass.length < 6) {
                    showSnackbar(getString(R.string.login_lenght_hit))
                    return
                }
                User(user, pass).login(loginListener)
            }
            R.id.ll_wechat_logo -> if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN)) {
                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, authListener)
            } else {
                showSnackbar(getString(R.string.apk_not_install))
            }
            R.id.ll_tencent_logo -> if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ)) {
                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.QQ, authListener)
            } else {
                showSnackbar(getString(R.string.apk_not_install))
            }
            R.id.ll_sina_logo -> if (UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.SINA)) {
                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.SINA, authListener)
            } else {
                showSnackbar(getString(R.string.apk_not_install))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            if (v.id == R.id.et_login_username) {
                iv_login_eyes.setImageResource(R.drawable.login_icon_eyes_open)
            } else if (v.id == R.id.et_login_password) {
                iv_login_eyes.setImageResource(R.drawable.login_icon_eyes_closed)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(getEditTextString(et_login_password)) && !TextUtils.isEmpty(getEditTextString(et_login_username))) {
            btn_login.isEnabled = true
            btn_login.isClickable = true
        } else {
            btn_login.isClickable = false
            btn_login.isEnabled = false
        }
    }

    override fun afterTextChanged(s: Editable) {}

    private fun synchronizationVideo(user: User?) {
        if (IMovieAppliction.app == null || user == null) return
        val query = BmobQuery<VideoCollect>()
        query.addWhereEqualTo("userId", user.objectId)
        query.findObjects(IMovieAppliction.app, findVideoListener)
    }

    private inner class SaveTaskListener(private val list: List<VideoCollect>?) : AsyTaskListenerImpl<Int>() {
        override fun onTaskBackground(): Int? {
            return if (liteOrm == null || list == null) 0 else liteOrm.insert(list)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            try {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
