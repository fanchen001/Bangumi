package com.fanchen.imovie.fragment;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.WebActivity;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.util.RegularUtil;

import butterknife.InjectView;

/**
 * Created by fanchen on 2017/10/11.
 */
public class BilijjFragment extends BaseFragment implements View.OnClickListener {

    @InjectView(R.id.ed_bilijj)
    EditText mBilijjEditText;
    @InjectView(R.id.btn_get_down)
    Button mGetDownButton;
    @InjectView(R.id.btn_new_video)
    Button mNewVideoButton;
    @InjectView(R.id.btn_new_music)
    Button mNewMusicButton;

    public static Fragment newInstance() {
        return new BilijjFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_bilijj;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mGetDownButton.setOnClickListener(this);
        mNewVideoButton.setOnClickListener(this);
        mNewMusicButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_down:
                String url = getEditTextString(mBilijjEditText);
                if(!TextUtils.isEmpty(url) && (RegularUtil.isAllNumric(url) || url.startsWith("http") || url.startsWith("https"))){
                    String[] split = url.split("/");
                    String aid = split[split.length - 1].replace("av", "");
                    WebActivity.startActivity(activity, String.format("http://www.jijidown.com/video/av%s",aid));
                }else{
                    showSnackbar(getString(R.string.url_error_hit));
                }
                break;
            case R.id.btn_new_video:
                WebActivity.startActivity(activity,"http://www.jijidown.com/new/video");
                break;
            case R.id.btn_new_music:
                WebActivity.startActivity(activity,"http://www.jijidown.com/new/music");
                break;
        }
    }
}
