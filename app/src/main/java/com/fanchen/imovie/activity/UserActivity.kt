package com.fanchen.imovie.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.dialog.OnButtonClickListener
import com.fanchen.imovie.entity.AppEvent
import com.fanchen.imovie.entity.bmob.User
import com.fanchen.imovie.entity.bmob.VideoCollect
import com.fanchen.imovie.picasso.PicassoWrap
import com.fanchen.imovie.util.DialogUtil
import kotlinx.android.synthetic.main.activity_user_space.*

class UserActivity : BaseToolbarActivity(), View.OnClickListener {
    override fun getLayout(): Int {
        return R.layout.activity_user_space
    }

    override val activityTitle: String
        get() = getString(R.string.my_space)

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        if (loginUser == null) {
            finish()
            return
        }
        setUserInfo(loginUser)
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onMainEvent(event: AppEvent) {
        if (AppEvent.UPDATE == event.what) {
            setUserInfo(loginUser)
        }
    }

    override fun setListener() {
        super.setListener()
        iv_user_iconset.setOnClickListener(this)
        rl_user_bindphone.setOnClickListener(this)
        rl_user_profile.setOnClickListener(this)
        rl_user_changepassword.setOnClickListener(this)
        rl_user_profile.setOnClickListener(this)
        tv_user_logout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_user_iconset -> {
            }
            R.id.rl_user_profile -> UserInfoActivity.startActivity(this)
            R.id.rl_user_bindphone -> if (loginUser != null) {
                if (!TextUtils.isEmpty(loginUser.phone)) {
                    showSnackbar(getString(R.string.error_bind))
                } else {
                    BindPhoneActivity.startActivity(this)
                }
            }
            R.id.rl_user_changepassword -> {
                if (loginUser == null) return
                if (loginUser.isAuthQQ || loginUser.isAuthWB || loginUser.isAuthWX) {
                    showSnackbar(getString(R.string.error_changepassword))
                    return
                }
            }
            R.id.tv_user_logout -> DialogUtil.showMaterialDialog(this, getString(R.string.logout_hit)) { dialog, btn ->
                dialog.dismiss()
                if (btn == OnButtonClickListener.RIGHT) {
                    User.logout()
                    liteOrm.delete(VideoCollect::class.java)
                    postAppEvent(AppEvent(UserActivity::class.java, AppEvent.LOGOUT))
                    finish()
                }
            }
        }
    }

    private fun setUserInfo(loginUser: User?) {
        if (loginUser == null) return
        tv_user_username.text = loginUser.nickName
        btn_user_birthday.text = loginUser.birthday
        when {
            loginUser.isAuthQQ -> iv_bindaccount_tencent.isSelected = true
            loginUser.isAuthWB -> iv_bindaccount_sina.isSelected = true
            loginUser.isAuthWX -> iv_bindaccount_weixin.isSelected = true
        }
        tv_bind_phone.text = if (TextUtils.isEmpty(loginUser.phone)) "未绑定" else loginUser.phone
        if (!TextUtils.isEmpty(loginUser.headerUrl) && appliction != null) {
            PicassoWrap(picasso).loadVertical(loginUser.headerUrl, iv_user_iconset)
        } else if (loginUser.header != null && appliction != null) {
            PicassoWrap(picasso).loadVertical(loginUser.header.getFileUrl(appliction), iv_user_iconset)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            try {
                val intent = Intent(context, UserActivity::class.java)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
