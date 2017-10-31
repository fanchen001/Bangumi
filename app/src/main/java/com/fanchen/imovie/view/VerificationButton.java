package com.fanchen.imovie.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * Created by fanchen on 2017/10/5.
 */
public class VerificationButton extends Button implements View.OnClickListener{

    public static final int MESSAGE_SUCCESS = 1;
    public static final int MESSAGE_COUNTDOWN = 2;

    private OnClickListener l;
    private CharSequence text;
    private Activity activity;
    private boolean stopDown = false;

    public VerificationButton(Context context) {
        super(context);
        super.setOnClickListener(this);
        if(context instanceof Activity)
            activity = (Activity) context;
    }

    public VerificationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnClickListener(this);
        if(context instanceof Activity)
            activity = (Activity) context;
    }

    public VerificationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnClickListener(this);
        if(context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.l = l;
    }

    public void setStopDown(boolean stopDown) {
        this.stopDown = stopDown;
    }

    @Override
    public void onClick(View v) {
        if(isClickable() && isEnabled()){
            if(this.l != null){
                l.onClick(v);
            }
            setClickable(false);
            setEnabled(false);
            text = getText();
            Message message = Message.obtain();
            message.what = MESSAGE_COUNTDOWN;
            message.obj = 60;
            mEventHandler.sendMessageDelayed(message,1000);
        }
    }

    private Handler mEventHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            if(stopDown || activity.isFinishing()){//
            // 用户手动取消倒计时
                stopDown = false;
                setEnabled(true);
                setClickable(true);
                setText(text);
                return;
            }
            switch (msg.what){
                case MESSAGE_COUNTDOWN:
                    Message message = Message.obtain();
                    Integer count = (Integer) msg.obj;
                    if(count == 1){
                        //倒计时结束
                        message.what = MESSAGE_SUCCESS;
                        mEventHandler.sendMessageDelayed(message,1000);
                    }else{
                        setText(String.valueOf(count));
                        message.what = MESSAGE_COUNTDOWN;
                        message.obj = --count;
                        mEventHandler.sendMessageDelayed(message,1000);
                    }
                    break;
                case MESSAGE_SUCCESS:
                    setEnabled(true);
                    setClickable(true);
                    setText(text);
                    break;
            }
        }
    };
}
