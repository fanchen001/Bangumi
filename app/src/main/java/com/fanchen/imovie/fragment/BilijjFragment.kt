package com.fanchen.imovie.fragment

import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import com.fanchen.imovie.R
import com.fanchen.imovie.activity.WebActivity
import com.fanchen.imovie.base.BaseFragment
import com.fanchen.imovie.util.RegularUtil
import kotlinx.android.synthetic.main.fragment_bilijj.*

class BilijjFragment : BaseFragment(), View.OnClickListener {

    override fun getLayout(): Int {
        return R.layout.fragment_bilijj
    }

    override fun setListener() {
        super.setListener()
        btn_get_down.setOnClickListener(this)
        btn_new_video.setOnClickListener(this)
        btn_new_music.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_get_down -> {
                val url = getEditTextString(ed_bilijj)
                if (!TextUtils.isEmpty(url) && (RegularUtil.isAllNumric(url) || url.startsWith("http") || url.startsWith("https"))) {
                    val split = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val aid = split[split.size - 1].replace("av", "")
                    WebActivity.startActivity(activity, String.format("http://www.jijidown.com/video/av%s", aid))
                } else {
                    showSnackbar(getStringFix(R.string.url_error_hit))
                }
            }
            R.id.btn_new_video -> WebActivity.startActivity(activity, "http://www.jijidown.com/new/video")
            R.id.btn_new_music -> WebActivity.startActivity(activity, "http://www.jijidown.com/new/music")
        }
    }

    companion object {
        fun newInstance(): Fragment {
            return BilijjFragment()
        }
    }
}
