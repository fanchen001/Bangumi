package com.fanchen.imovie.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.util.AppUtil
import com.fanchen.imovie.util.SystemUtil
import kotlinx.android.synthetic.main.activity_about.*


/**
 * 应用关于
 */
class AboutActivity : BaseToolbarActivity(), View.OnClickListener {
    override fun getLayout(): Int {
        return R.layout.activity_about
    }

    override val activityTitle: String
        get() = getString(R.string.about)

    override fun setListener() {
        super.setListener()
        rl_btn_web.setOnClickListener(this)
        rl_btn_qqnum.setOnClickListener(this)
        rl_btn_phonenum.setOnClickListener(this)
        rl_btn_emailadress.setOnClickListener(this)
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        tv_version.text = String.format(getString(R.string.version_format), AppUtil.getVersionName(this))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rl_btn_web -> {
            }
            R.id.rl_btn_qqnum -> {
                // 打开QQ群介绍界面(对QQ群号)
                showToast("正在打开QQ...")
                val url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=541361788&card_type=group&source=qrcode"
                SystemUtil.startThreeApp(this, url)
            }
            R.id.rl_btn_phonenum -> {
            }
            R.id.rl_btn_emailadress -> try {
                val emailmunber = (rl_btn_emailadress.getChildAt(1) as TextView).text.toString()
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822" // 设置邮件格式
                intent.putExtra(Intent.EXTRA_EMAIL, emailmunber) // 接收人
                intent.putExtra(Intent.EXTRA_CC, emailmunber) // 抄送人
                intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分") // 主题
                intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分") // 正文
                startActivity(Intent.createChooser(intent, "请选择邮件类应用"))
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackbar(getString(R.string.activity_not_found))
            }
        }
    }
}
