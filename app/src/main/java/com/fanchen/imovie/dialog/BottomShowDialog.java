package com.fanchen.imovie.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.util.SystemUtil;


/**
 *
 * Created by fanchen on 2017/7/27.
 */
public class BottomShowDialog extends BottomBaseDialog<BottomShowDialog> implements View.OnClickListener {

    private TextView mTextView;
    private Button mButton;
    private View.OnClickListener listener;
    private int layout;

    public BottomShowDialog(Context context, View animateView, View.OnClickListener listener) {
        super(context, animateView);
        this.listener = listener;
    }

    public BottomShowDialog(Context context, View.OnClickListener listener) {
        super(context);
        this.listener = listener;
    }

    public BottomShowDialog(Context context) {
        super(context);
    }

    public BottomShowDialog(Context context, int layout) {
        super(context);
        this.layout = layout;
    }

    public BottomShowDialog(Context context,int layout, View.OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.layout = layout;
    }

    @Override
    public View onCreateView() {
        View inflate = View.inflate(getContext(),layout == 0 ? R.layout.dialog_code_cancle :layout, null);
        mTextView = (TextView) inflate.findViewById(R.id.tv_phone);
        mButton = (Button) inflate.findViewById(R.id.btn_cancle);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        mTextView.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_phone:
                if(listener != null){
                    listener.onClick(v);
                }else if(context instanceof BaseActivity){
                    // 打开QQ群介绍界面(对QQ群号)
                    ((BaseActivity)context).showToast("正在打开QQ...");
                    String url = "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=541361788&card_type=group&source=qrcode";
                    SystemUtil.startThreeApp((BaseActivity)context,url);
                }
                break;
            case R.id.btn_cancle:
                dismiss();
                break;
        }
    }
}
